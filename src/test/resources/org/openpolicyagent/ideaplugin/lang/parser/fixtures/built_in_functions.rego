package test

x:=1
y:=2
set:={}
object1:={}
string:=""
number:=5

comparisons {
    x == y
    x != y
    x < y
    x <= y
    x > y
    x >= y
}

numbers {
	collection_or_string:={"",""}
	array_or_set:=[]

    z := x + y
    z1 := x - y
    z2 := x * y
    z3 := x / y
    z4 := x % y
    output := round(x)
    output1 := abs(x)
    output2 := count(collection_or_string)
    output3 := sum(array_or_set)
    output4 := product(array_or_set)
    output5 := max(array_or_set)
    output6 := min(array_or_set)
    output7 := sort(array_or_set)
    output8 := all(array_or_set)
    output9 := any(array_or_set)
}

arrays {
	array1:=[]
	array2:=[]
    stopIndex:=1
	startIndex:=2


    output1 := array.concat(array1, array2)
    output2 := array.slice(array1, startIndex, stopIndex)
}

sets {
	s1:={""}
    s2:={""}

    s3 := s1 & s2
    s4 := s1 | s2
    s5 := s1 - s2
    output := intersection(set[set])
    output1 := union(set[set])
}

objects {
	object1:={}
    key:={"":""}
	keys:={"":""}
	paths:=["a/b"]

    value := object.get(object1, key, "default")
    output := object.remove(object1, keys)
    output1 := object.union(object1, object1)
    filtered := object.filter(object1, keys)
    filtered1 := json.filter(object1, paths)
    output2 := json.remove(object1, paths)
}

strings_rule {
	array_or_set:=[]
    delimiter:=""
	search:=""
	base:=10
	old:=""
	new:=""
    values:=[""]
	start:=1
	length:=1
	cutset:=""
	prefix:=""
	suffix:=""
    patterns={}

    output := concat(delimiter, array_or_set)
    contains(string, search)
    endswith(string, search)
    output1 := format_int(number, base)
    output2 := indexof(string, search)
    output3 := lower(string)
    output4 := replace(string, old, new)
    output5 := strings.replace_n(patterns, string)
    output6 := split(string, delimiter)
    output7 := sprintf(string, values)
    startswith(string, search)
    output8 := substring(string, start, length)
    output9 := trim(string, cutset)
    output10 := trim_left(string, cutset)
    output11 := trim_prefix(string, prefix)
    output12 := trim_right(string, cutset)
    output13 := trim_suffix(string, suffix)
    output14 := trim_space(string)
    output15 := upper(string)
}

regex_rule {
	value:=""
	pattern:=""
	glob1:=""
	glob2:=""
	delimiter_end:=""
	delimiter_start:=""

    re_match(pattern, value)
    output := regex.split(pattern, string)
    regex.globs_match(glob1, glob2)
    output2 := regex.template_match(pattern, string, delimiter_start, delimiter_end)
    output3 := regex.find_n(pattern, string, number)
    output4 := regex.find_all_string_submatch_n("pattern", "string", 1)
}

globFunc {
	value:={}
	pattern:=""
    match:=""
	delimiters:=[""]

    output := glob.match(pattern, delimiters, match)
    output1 := glob.quote_meta(pattern)
    output2 := glob.match("*.github.com", [], "api.github.com")
    output3 := glob.match("*.github.com", [], "api.cdn.github.com")
    output4 := glob.match("*:github:com", [":"], "api:github:com")
    output5 := glob.match("api.**.com", [], "api.github.com")
    output6 := glob.match("api.**.com", [], "api.cdn.github.com")
    output7 := glob.match("?at", [], "cat")
    output8 := glob.match("?at", [], "at")
    output9 := glob.match("[abc]at", [], "bat")
    output10 := glob.match("[abc]at", [], "cat")
    output11 := glob.match("[abc]at", [], "lat")
    output12 := glob.match("[!abc]at", [], "cat")
    output13 := glob.match("[!abc]at", [], "lat")
    output14 := glob.match("[a-c]at", [], "cat")
    output15 := glob.match("[a-c]at", [], "lat")
    output16 := glob.match("[!a-c]at", [], "cat")
    output17 := glob.match("[!a-c]at", [], "lat")
    output18 := glob.match("{cat,bat,[fr]at}", [], "cat")
    output19 := glob.match("{cat,bat,[fr]at}", [], "bat")
    output20 := glob.match("{cat,bat,[fr]at}", [], "rat")
    output21 := glob.match("{cat,bat,[fr]at}", [], "at")
}

bitwise {
    s:=5
    z := bits.or(x, y)
    z1 := bits.and(x, y)
    z2 := bits.negate(x)
    z3 := bits.xor(x, y)
    z4 := bits.lsh(x, s)
    z5 := bits.rsh(x, s)
}

conversions {
    output := to_number(x)
}

unitsFunc {
    output := units.parse_bytes(string)
}

types {
	x:={}

    output := is_number(x)
    output1 := is_string(x)
    output2 := is_boolean(x)
    output3 := is_array(x)
    output4 := is_set(x)
    output5 := is_object(x)
    output6 := is_null(x)
    output7 := type_name(x)
}

encoding {
	x:=""

    output := base64.encode(x)
    output1 := base64.decode(string)
    output2 := base64url.encode(x)
    output3 := base64url.decode(string)
    output4 := urlquery.encode(string)
    output5 := urlquery.encode_object(object1)
    output6 := urlquery.decode(string)
    output7 := json.marshal(x)
    output8 := json.unmarshal(string)
    output9 := yaml.marshal(x)
    output10 := yaml.unmarshal(string)
}

tokenSigning {
    io.jwt.encode_sign({
        "typ": "JWT",
        "alg": "HS256"
    }, {
        "iss": "joe",
        "exp": 1300819380,
        "aud": ["bob", "saul"],
        "http://example.com/is_root": true,
        "privateParams": {
            "private_one": "one",
            "private_two": "two"
        }
    }, {
        "kty": "oct",
        "k": "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow"
    })

    io.jwt.encode_sign({
        "typ": "JWT",
        "alg": "HS256"},
        {}, {
        "kty": "oct",
        "k": "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow"
    })

    io.jwt.encode_sign({
        "alg": "RS256"
    }, {
        "iss": "joe",
        "exp": 1300819380,
        "aud": ["bob", "saul"],
        "http://example.com/is_root": true,
        "privateParams": {
            "private_one": "one",
            "private_two": "two"
        }
    },
    {
        "kty": "RSA",
        "n": "ofgWCuLjybRlzo0tZWJjNiuSfb4p4fAkd_wWJcyQoTbji9k0l8W26mPddxHmfHQp-Vaw-4qPCJrcS2mJPMEzP1Pt0Bm4d4QlL-yRT-SFd2lZS-pCgNMsD1W_YpRPEwOWvG6b32690r2jZ47soMZo9wGzjb_7OMg0LOL-bSf63kpaSHSXndS5z5rexMdbBYUsLA9e-KXBdQOS-UTo7WTBEMa2R2CapHg665xsmtdVMTBQY4uDZlxvb3qCo5ZwKh9kG4LT6_I5IhlJH7aGhyxXFvUK-DWNmoudF8NAco9_h9iaGNj8q2ethFkMLs91kzk2PAcDTW9gb54h4FRWyuXpoQ",
        "e": "AQAB",
        "d": "Eq5xpGnNCivDflJsRQBXHx1hdR1k6Ulwe2JZD50LpXyWPEAeP88vLNO97IjlA7_GQ5sLKMgvfTeXZx9SE-7YwVol2NXOoAJe46sui395IW_GO-pWJ1O0BkTGoVEn2bKVRUCgu-GjBVaYLU6f3l9kJfFNS3E0QbVdxzubSu3Mkqzjkn439X0M_V51gfpRLI9JYanrC4D4qAdGcopV_0ZHHzQlBjudU2QvXt4ehNYTCBr6XCLQUShb1juUO1ZdiYoFaFQT5Tw8bGUl_x_jTj3ccPDVZFD9pIuhLhBOneufuBiB4cS98l2SR_RQyGWSeWjnczT0QU91p1DhOVRuOopznQ",
        "p": "4BzEEOtIpmVdVEZNCqS7baC4crd0pqnRH_5IB3jw3bcxGn6QLvnEtfdUdiYrqBdss1l58BQ3KhooKeQTa9AB0Hw_Py5PJdTJNPY8cQn7ouZ2KKDcmnPGBY5t7yLc1QlQ5xHdwW1VhvKn-nXqhJTBgIPgtldC-KDV5z-y2XDwGUc",
        "q": "uQPEfgmVtjL0Uyyx88GZFF1fOunH3-7cepKmtH4pxhtCoHqpWmT8YAmZxaewHgHAjLYsp1ZSe7zFYHj7C6ul7TjeLQeZD_YwD66t62wDmpe_HlB-TnBA-njbglfIsRLtXlnDzQkv5dTltRJ11BKBBypeeF6689rjcJIDEz9RWdc",
        "dp": "BwKfV3Akq5_MFZDFZCnW-wzl-CCo83WoZvnLQwCTeDv8uzluRSnm71I3QCLdhrqE2e9YkxvuxdBfpT_PI7Yz-FOKnu1R6HsJeDCjn12Sk3vmAktV2zb34MCdy7cpdTh_YVr7tss2u6vneTwrA86rZtu5Mbr1C1XsmvkxHQAdYo0",
        "dq": "h_96-mK1R_7glhsum81dZxjTnYynPbZpHziZjeeHcXYsXaaMwkOlODsWa7I9xXDoRwbKgB719rrmI2oKr6N3Do9U0ajaHF-NKJnwgjMd2w9cjz3_-kyNlxAr2v4IKhGNpmM5iIgOS1VZnOZ68m6_pbLBSp3nssTdlqvd0tIiTHU",
        "qi": "IYd7DHOhrWvxkwPQsRM2tOgrjbcrfvtQJipd-DlcxyVuuM9sQLdgjVk2oy26F0EmpScGLq2MowX7fhd_QJQ3ydy5cY7YIBi87w93IKLEdfnbJtoOPLUW0ITrJReOgo1cq9SbsxYawBgfp_gh6A5603k2-ZQwVK0JKSHuLFkuQ3U"
    })

    io.jwt.encode_sign_raw(
        `{"typ":"JWT","alg":"HS256"}`,
         `{"iss":"joe","exp":1300819380,"http://example.com/is_root":true}`,
        `{"kty":"oct","k":"AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow"}`
    )
}

tokenVerification {
    constraints:={}
	certificate:=""
    secret:=""

    output := io.jwt.verify_rs256(string, certificate)
    output1 := io.jwt.verify_rs384(string, certificate)
    output2 := io.jwt.verify_rs512(string, certificate)
    output3 := io.jwt.verify_ps256(string, certificate)
    output4 := io.jwt.verify_ps384(string, certificate)
    output5 := io.jwt.verify_ps512(string, certificate)
    output6 := io.jwt.verify_es256(string, certificate)
    output7 := io.jwt.verify_es384(string, certificate)
    output8 := io.jwt.verify_es512(string, certificate)
    output9 := io.jwt.verify_hs256(string, secret)
    output10 := io.jwt.verify_hs384(string, secret)
    output11 := io.jwt.verify_hs512(string, secret)
    output12 := io.jwt.decode(string)
    output13 := io.jwt.decode_verify(string, constraints)
}

timeFunc {
	value:=""
	layout:=""
	duration:=""
	ns:=5
	tz:=5
	years:=5
	months:=5
	days:=5

    output := time.now_ns()
    output1 := time.parse_ns(layout, value)
    output2 := time.parse_rfc3339_ns(value)
    output3 := time.parse_duration_ns(duration)
    output4 := time.date(ns)
    output5 := time.date([ns, tz])
    output6 := time.clock(ns)
    output7 := time.clock([ns, tz])
    day := time.weekday(ns)
    day1 := time.weekday([ns, tz])
    output8 := time.add_date(ns, years, months, days)
}

cryptography {
    output := crypto.x509.parse_certificates(string)
    output1 := crypto.md5(string)
    output2 := crypto.sha1(string)
    output3 := crypto.sha256(string)

}

graphs {
    walk(x, [path, value])
}

entity_name:={}
org_chart_data:={}

graphs2[entity_name] = edges {
    org_chart_data[entity_name]
    edges := {neighbor | org_chart_data[neighbor].owner == entity_name}
}

graphs2[entity_name] = access {
    org_chart_graph:={}
    org_chart_data[entity_name]
    reachable := graph.reachable(org_chart_graph, {entity_name})
    access := {item | reachable[k]; item := org_chart_data[k].access[_]}
}

httpFunc {
    response := http.send({"method": "get", "url": "https://www.google.com"})
}

networking {
	cidr2:=""
	cidr1:=""
	cidr:=""
	cidr_or_ip:=""

    net.cidr_contains_matches("1.1.1.0/24", "1.1.1.128")
    net.cidr_contains_matches(["1.1.1.0/24", "1.1.2.0/24"], "1.1.1.128")
    net.cidr_contains_matches([["1.1.0.0/16", "foo"], "1.1.2.0/24"], ["1.1.1.128", ["1.1.254.254", "bar"]])
    net.cidr_contains_matches({["1.1.0.0/16", "foo"], "1.1.2.0/24"}, {"x": "1.1.1.128", "y": ["1.1.254.254", "bar"]})
    net.cidr_intersects(cidr1, cidr2)
    net.cidr_contains(cidr, cidr_or_ip)
    net.cidr_expand(cidr)
}

uuidFunc {
    output := uuid.rfc4122(string)
}

regoFunc {
	filename:=""
    output := rego.parse_module(filename, string)
}

opaFunc {
    output := opa.runtime()
}

debugging {
    trace(string)
}
