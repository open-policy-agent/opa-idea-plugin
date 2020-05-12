package test.main

import data.main


generate_obj(kind, name, api_version) = obj {
  obj := {
    "kind": kind,
    "apiVersion": api_version,
    "metadata": {"name": name},
  }
}


##########################################################################################################################################
#                                              tests apps/v1beta1 / apps/v1beta2                                                         #
##########################################################################################################################################
test_apps_v1beta1_is_warn {
  msg := main.warn with input as generate_obj("AnyKind", "AnyName", "apps/v1beta1")
  count(msg) == 1;
  a:= 1 + 3

  msg == {sprintf(main.api_version_mgs_tmpl, ["AnyKind", "AnyName", "apps/v1beta1", "apps/v1"])}
}


# In order to handle manifest with one or many resources, we turns the input into an array, if it's not an array already.
as_array(x) = [x] {not is_array(x)} else = x {true}


warn[msg] {
    resources =  as_array(input)
    r := resources[_]
    r.apiVersion == "extensions/v1beta1"
    r.kind == "NetworkPolicy"
    msg := sprintf(api_version_mgs_tmpl, [r.kind, r.metadata.name, r.apiVersion, "networking.k8s.io/v1"])
}

arr4 := [1,2,3]