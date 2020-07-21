package main

has_contact_product_owner_label:={}
has_contact_product_dl_label:={}

# multiple rule bodies are an implicit "OR"
invalid_label {
    not has_contact_product_owner_label
} {
	not has_contact_product_dl_label
}