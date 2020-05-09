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
  count(msg) == 1
  msg == {sprintf(main.api_version_mgs_tmpl, ["AnyKind", "AnyName", "apps/v1beta1", "apps/v1"])}
}

test_apps_v1beta2_is_warn {
  msg := main.warn with input as generate_obj("AnyKind", "AnyName", "apps/v1beta2")
  count(msg) == 1
  msg == {sprintf(main.api_version_mgs_tmpl, ["AnyKind", "AnyName", "apps/v1beta2", "apps/v1"])}
}

test_apps_v1_is_ok {
  msg := main.warn with input as generate_obj("AnyKind", "AnyName", "apps/v1")
  count(msg) == 0
}

