package test.main

import data.main


<HEAD>generate_obj</HEAD>(kind, name, api_version) = obj {
  obj := {
    "kind": kind,
    "apiVersion": api_version,
    "metadata": {"name": name},
  }
}


##########################################################################################################################################
#                                              tests apps/v1beta1 / apps/v1beta2                                                         #
##########################################################################################################################################
<HEAD>test_apps_v1beta1_is_warn</HEAD> {
  msg := main.warn with input as <CALL>generate_obj</CALL>("AnyKind", "AnyName", "apps/v1beta1")
  <CALL>count</CALL>(msg) == 1;
  a:= 1 + 3

  msg == {<CALL>sprintf</CALL>(main.api_version_mgs_tmpl, ["AnyKind", "AnyName", "apps/v1beta1", "apps/v1"])}
}


# In order to handle manifest with one or many resources, we turns the input into an array, if it's not an array already.
<CALL>as_array</CALL>(x) = [x] {not <CALL>is_array</CALL>(x)} else = x {true}


<HEAD>warn</HEAD>[msg] {
    resources =  <CALL>as_array</CALL>(input)
    r := resources[_]
    r.apiVersion == "extensions/v1beta1"
    r.kind == "NetworkPolicy"
    msg := <CALL>sprintf</CALL>(api_version_mgs_tmpl, [r.kind, r.metadata.name, r.apiVersion, "networking.k8s.io/v1"])
}

<HEAD>arr4</HEAD> := [1,2,3]