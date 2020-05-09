package main

# multiple rule bodies are an implicit "OR"
invalid_label {
    not has_contact_product_owner_label
} {
	not has_contact_product_dl_label
}