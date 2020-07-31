package some_package

import data.main

test_apps_v1beta1_in_package_c {
  msg := main.warn with input as generate_obj("AnyKind", "AnyName", "apps/v1beta1")
  count(msg) == 1
  msg == {sprintf(main.api_version_mgs_tmpl, ["AnyKind", "AnyName", "apps/v1beta1", "apps/v1"])}
}

generate_obj(a, b, c) = r{
 r = {a: a, b: b, c: c}
}