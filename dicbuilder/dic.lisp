(in-package :dic)

(package-alias :dic.trie :trie)
(package-alias :dic.trie.double-array :double-array)

(defmacro each-entry-first-line ((line position filepath) &body body)
  `(with-open-file (#1=#:in ,filepath :external-format dic:*text-dictionary-charset*)
     (loop WITH #2=#:first = t
           FOR ,line OF-TYPE (or null simple-string) = (read-line #1# nil nil)
           WHILE ,line
       DO
       (when #2#
         (let ((,position (file-position #1#)))
           ,@body)
         (setf #2# nil))
       (when (string= ,line *end-of-entry*)
         (setf #2# t)))))

(defun collect-keys (text-dic &aux keys)
  (each-entry-first-line (key _ text-dic)
    (declare (ignore _))
    (push key keys))
  (unique (sort keys #'string<)))

(defun make-id=>offsets (text-dic key-count da)
  (let ((map (make-array key-count :initial-element nil)))
    (each-entry-first-line (key offset text-dic)
      (let ((id (double-array:get-id key da)))
        (push offset (aref map id))))

    (loop FOR i FROM 0 BELOW (length map)
          FOR offsets = (aref map i)
      DO
      (setf (aref map i) (nreverse offsets)))
    map))

(defun store-id=>offsets (map output-dir &aux (*default-pathname-defaults* output-dir))
  (with-open-binary-output-file (out.id2off *path.id2offsets*)
    (with-open-binary-output-file (out.offs *path.offsets*)
      (loop FOR offsets ACROSS map
        DO
        (write-int (file-position out.offs) out.id2off :width 4)
        (dolist (offset offsets)
          (write-int offset out.offs :width 4))
        FINALLY
        (write-int (file-position out.offs) out.id2off :width 4)))))

(defun build (text-dic output-dir &aux (output-dir (pathname output-dir)))
  (ensure-directories-exist output-dir)
  (let* ((keys 
          (with-time "collect key:" 
            (collect-keys text-dic)))
         (da
          (with-time "build index:"
            (double-array:build keys output-dir)
            (double-array:load-dic output-dir)))
         (id=>offsets 
          (with-time "make id => offsets:"
            (make-id=>offsets text-dic (length keys) da))))
    (with-time "store id => offsets:"
      (store-id=>offsets id=>offsets output-dir))
    (with-time "copy data"
      (copy-file text-dic (merge-pathnames *path.data* output-dir))))
  'done)

(package-alias :dic.trie)
(package-alias :dic.trie.double-array)
