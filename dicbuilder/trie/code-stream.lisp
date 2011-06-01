;;;; TODO: サロゲートペア対応
(defpackage dic.trie.code-stream
  (:use :common-lisp :dic)
  (:shadow :common-lisp read peek)
  (:export code-stream
           make
           eos?
           peek
           eat
           read))
(in-package :dic.trie.code-stream)

(declaim (inline make-code-stream make eos? peek eat read))

(defstruct code-stream
  (src 0 :type simple-string)
  (pos 0 :type fixnum)
  (end 0 :type fixnum))

(defun make (string &key (start 0) (end (length string)))
  (make-code-stream :src string :pos start :end end))

(defun eos? (in)
  (with-slots (pos end) (the code-stream in)
    (= pos end)))

(defun peek (in)
  (with-slots (src pos) (the code-stream in)
    (char-code (char src pos))))

(defun eat (in)
  (with-slots (pos) (the code-stream in)
    (unless (eos? in)
      (incf pos)))
  in)

(defun read (in)
  (prog1 (peek in)
    (eat in)))
