(defpackage dic.trie
  (:use :common-lisp :dic.util)
  (:export build
           node-options
           collect-children))
(in-package :dic.trie)

(package-alias :dic.trie.code-stream :stream)

(declaim #.dic::*fastest*)
(deftype positive-fixnum () '(integer 0 #.most-positive-fixnum))

;;;;;;;;;;;;;;;;;;;;
;;; utility function
(declaim (inline fixnumize))
(defun fixnumize (n)
  (declare (sb-ext:muffle-conditions sb-ext:compiler-note))
  (ldb (byte #.(integer-length most-positive-fixnum) 0) n))

;;;;;;;;
;;; node
(defstruct node
  (label         0 :type (unsigned-byte 16))
  (terminal?   nil :type boolean)
  (child       nil :type (or null node))
  (sibling     nil :type (or null node))
  (child-total   0 :type positive-fixnum) ; amount of child side nodes
  (sibling-total 0 :type positive-fixnum) ; amount of sibling side nodes
  (hash         -1 :type fixnum))

;;;;;;;;;;;;;;;;;;;;;;
;;; auxiliary function
(macrolet ((calc-xxx-total (node slot)
             `(with-slots (,slot) (the node ,node)
                (if (null ,slot)
                    0
                  (the fixnum
                       (+ (if (node-terminal? ,slot) 1 0)
                          (node-child-total ,slot) (node-sibling-total ,slot)))))))
  (defun calc-child-total (node) (calc-xxx-total node child))
  (defun calc-sibling-total (node) (calc-xxx-total node sibling)))

;;;;;;;;;;;;;;;;;
;;; hash function
(defun node= (n1 n2)
  (and (eq (node-child n1) (node-child n2))
       (eq (node-sibling n1) (node-sibling n2))
       (= (node-label n1) (node-label n2))
       (eq (node-terminal? n1) (node-terminal? n2))))

(declaim (ftype (function (node) positive-fixnum) sxhash-node))
(defun sxhash-node (node)
  (if (null node)
      #.(sxhash nil)
    (with-slots (hash child-total sibling-total) (the node node)
      (when (= -1 hash)
        (setf hash (logxor (sxhash (node-label node))
                           (sxhash (node-terminal? node))
                           (fixnumize (* (sxhash-node (node-child node)) 7))
                           (fixnumize (* (sxhash-node (node-sibling node)) 13))))
        (setf child-total (calc-child-total node)
              sibling-total (calc-sibling-total node)))
      hash)))

(sb-ext:define-hash-table-test node= sxhash-node)

;;;;;;;;;;;;;;;;;;
;;; build function
(defun share (node memo)
  (if (null node)
      nil
    (or (gethash node memo)
        (progn 
          (setf (node-child node) (share (node-child node) memo)
                (node-sibling node) (share (node-sibling node) memo))
          (gethash node memo))
        (setf (gethash node memo) node))))

(defun push-child (in parent)
  (if (stream:eos? in)
      (setf (node-terminal? parent) t)
    (let ((new-node (make-node :label (stream:read in))))
      (shiftf (node-sibling new-node) (node-child parent) new-node)
      (push-child in new-node))))

(defun insert (in parent memo)
  (let ((node (node-child parent)))
    (if (or (null node)
            (stream:eos? in)
            (/= (stream:peek in) (node-label node)))
        (progn
          (setf (node-child parent) (share node memo))
          (push-child in parent))
      (insert (stream:eat in) node memo))))

(defun unique (keys)
  (loop FOR (prev cur) ON keys
        WHILE cur
        UNLESS (string= (the simple-string prev) (the simple-string cur))
    COLLECT cur INTO list
    FINALLY
    (return (cons (first keys) list))))
        
(defun build (keys)
  (let ((trie (make-node))
        (memo (make-hash-table :test #'node=)))
    (dolist (key (unique (sort keys #'string<)) (share trie memo))
      (let ((in (stream:make key)))
        (declare (dynamic-extent in))
        (insert in trie memo)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; other external function
(defun node-options (node)
  "Encode terminal? and sibling-total fields into fixnum"
  (with-slots (terminal? sibling-total) (the node node)
    (fixnumize
     (+ (if terminal? 1 0)
        (ash sibling-total 1)))))

(defun element-count (node)
  (with-slots (terminal? child-total) (the node node)
    (the fixnum (+ (if terminal? 1 0) child-total))))
        
(declaim (ftype (function (node) list) collect-children))
(defun collect-children (node)
  (loop WITH acc = '()
        FOR child = (node-child node)
               THEN (node-sibling child)
        WHILE child
    DO
    (push child acc)
    FINALLY
    (return acc)))

(package-alias :dic.trie.code-stream)