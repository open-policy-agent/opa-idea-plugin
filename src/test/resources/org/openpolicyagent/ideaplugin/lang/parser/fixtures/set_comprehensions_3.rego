package main

k:= {}
a:= {}
b:= {}
c:= {}

merge_objects(a, b) = c {
    ks := {k | some k; _ = a[k]} | {k | some k; _ = b[k]}
    d := {k: v | some k; ks[k]; v := object.get(a, a[b], "default")}
}
