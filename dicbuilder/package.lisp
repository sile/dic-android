(defpackage dic.util
  (:use :common-lisp)
  (:export write-int
           read-int
           package-alias
           nlet
           each-line
           a.if
           it))

(defpackage dic
  (:use :common-lisp :dic.util)
  (:export build
           *text-dictionary-charset*

           done))
(in-package :dic)

(deftype octet () '(unsigned-byte 8))
(defvar *text-dictionary-charset* :utf-8)
(defvar *end-of-entry* "-=+=-=+=-=+=-=+=-=+=-")
(defvar *fastest* '(optimize (speed 3) (safety 0) (debug 0)))
