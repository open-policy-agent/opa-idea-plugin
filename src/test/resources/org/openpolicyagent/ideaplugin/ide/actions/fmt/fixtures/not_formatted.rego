package play

labelrego[msg] {
    input.request.kind.kind == "Deployment"
    input.request.operation == "CREATE"
    input.request.namespace == "labeltest2"

    invalid_label

    namespace = input.request.namespace
    msg := sprintf("[Deployment Rule]: Deployment Label is invalid or does not exist for namespace %q", [namespace])
}

# multiple rule bodies are an implicit "OR"
invalid_label {
    not has_contact_product_owner_label
} {
	not has_contact_product_dl_label
}

has_contact_product_owner_label {
	value := input.request.object.metadata["labels"]["contact.productowner"]
	re_match("^.+_at_domain\\.com$", value)
}

has_contact_product_dl_label {
	value := input.request.object.metadata["labels"]["contact.dl"]
	re_match("^.+_at_domain\\.com$", value)
}