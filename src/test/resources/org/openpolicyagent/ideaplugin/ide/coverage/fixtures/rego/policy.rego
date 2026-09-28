# Minimal policy example for exercising IR coverage by hand.
#
# command to generate a new bundle from this file
#
#     opa build -t plan -b -e example/allow \
#       -o build/opa-ir-coverage-fixture/bundle.tar.gz \
#       --plan-addons=unplanned_rules \
#       src/test/resources/org/openpolicyagent/ideaplugin/ide/coverage/fixtures/rego
package example

# Reached as the example/allow entrypoint
allow if {
	input.user == "alice"
	data.example.subpkg.known_user
}

# Not reachable from any entrypoint, only markable as not_covered
# when --plan-addons=unplanned_rules is set at build time.
unplanned if {
	input.user == "nobody"
}
