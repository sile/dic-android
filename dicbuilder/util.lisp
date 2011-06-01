(in-package :dic.util)

(declaim (inline write-int read-int))

(defun write-int (int stream &key (width 1))
  (declare ((member 1 2 4 8) width))
  (flet ((write-impl (pos) 
           (write-byte (ldb (byte 8 (* pos 8)) int) stream)))
    (declare (inline write-impl))
    (loop FOR i FROM (1- width) DOWNTO 0 DO (write-impl i))))

(defun read-int (in &key (width 1))
  (declare ((member 1 2 4 8) width))
  (loop FOR i FROM (1- width) DOWNTO 0
        SUM (ash (read-byte in) (* i 8))))

(defmacro package-alias (package &rest alias-list)
  `(eval-when (:compile-toplevel :load-toplevel :execute)
     (rename-package ,package ,package ',alias-list)))

(defmacro nlet (fn-name letargs &body body)
  `(labels ((,fn-name ,(mapcar #'car letargs)
              ,@body))
     (,fn-name ,@(mapcar #'cadr letargs))))

(defmacro each-line ((line filepath) &body body)
  `(with-open-file (#1=#:in ,filepath :external-format dic:*text-dictionary-charset*)
     (loop FOR ,line OF-TYPE (or null simple-string) = (read-line #1# nil nil)
           WHILE ,line
       DO
       (locally ,@body))))

(defmacro a.if (exp then else)
  `(let ((it ,exp))
     (if it
         ,then
       ,else)))
