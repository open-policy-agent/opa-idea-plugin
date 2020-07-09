package test

authorize = "allow" {
    input.user == "superuser"           # allow 'superuser' to perform any operation.
} else = "deny" {
    input.path[0] == "admin"            # disallow 'admin' operations...
    input.source_network == "external"  # from external networks.
} # ... more rules