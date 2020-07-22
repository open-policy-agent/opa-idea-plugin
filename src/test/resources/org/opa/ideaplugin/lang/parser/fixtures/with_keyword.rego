package main

warn:={}
api_version_mgs_tmpl = "%s %s - use deprecated apiversion '%s'.This api will be remove in kubernetes v1.16. Use apiversion '%s' instead. For more information see https://github.com/kubernetes/kubernetes/blob/master/CHANGELOG-1.16.md#action-required-3"

test_apps_v1beta1_in_package_c {
  msg := warn with input as generate_obj("AnyKind", "AnyName", "apps/v1beta1")
  count(msg) == 1
  #msg == {sprintf(api_version_mgs_tmpl, ["AnyKind", "AnyName", "apps/v1beta1", "apps/v1"])}
}

generate_obj(a,b,c) = z{
	z :=true
}