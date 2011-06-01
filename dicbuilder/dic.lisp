(in-package :dic)

(package-alias :dic.trie :trie)
(package-alias :dic.trie.double-array :double-array)

(defun collect-keys (text-dic &aux keys (first t))
  (each-line (line text-dic)
    (when first
      (push line keys)
      (setf first nil))
    (when (string= line *end-of-entry*)
      (setf first t)))
  keys)

(defun build (text-dic output-dir)
  (ensure-directories-exist output-dir)
  (double-array:build (collect-keys text-dic) output-dir)
  'done)

(package-alias :dic.trie)
(package-alias :dic.trie.double-array)
