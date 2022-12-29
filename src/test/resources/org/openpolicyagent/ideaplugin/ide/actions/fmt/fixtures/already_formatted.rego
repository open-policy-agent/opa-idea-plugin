package main

api_version_mgs_tmpl = "%s %s - use deprecated apiversion '%s'.This api will be remove in kubernetes v1.16. Use apiversion '%s' instead. For more information see https://github.com/kubernetes/kubernetes/blob/master/CHANGELOG-1.16.md#action-required-3"

# In order to handle manifest with one or many resources, we turns the input into an array, if it's not an array already.
as_array(x) = [x] {
	not is_array(x)
}

else = x

arr := [1, 2]

warn[msg] {
	a := 2
	apiversions = ["apps/v1beta1", "apps/v1beta2"]
	resources = as_array(input)
	r := resources[_]
	not r.apiVersion == apiversions[_]
	msg := sprintf(api_version_mgs_tmpl, [r.kind, r.metadata.name, r.apiVersion, "apps/v1"])
}

warn[msg] {
	kinds = ["DaemonSet", "Deployment", "ReplicaSet"]
	resources = as_array(input)
	r := resources[_]
	r.apiVersion == "extensions/v1beta1"
	r.kind == kinds[_]
	msg := sprintf(api_version_mgs_tmpl, [r.kind, r.metadata.name, r.apiVersion, "apps/v1"])
}

warn[msg] {
	resources = as_array(input)
	r := resources[_]
	r.apiVersion == "extensions/v1beta1"
	r.kind == "NetworkPolicy"
	msg := sprintf(api_version_mgs_tmpl, [r.kind, r.metadata.name, r.apiVersion, "networking.k8s.io/v1"])
}

warn[msg] {
	resources = as_array(input)
	r := resources[_]
	r.apiVersion == "extensions/v1beta1"
	r.kind == "PodSecurityPolicy"
	msg := sprintf(api_version_mgs_tmpl, [r.kind, r.metadata.name, r.apiVersion, "policy/v1beta1"])
}
