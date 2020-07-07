package test

comparisons {
    x == y
    x != y
    x < y
    x <= y
    x > y
    x >= y
}

numbers {
    z := x + y
    z := x - y
    z := x * y
    z := x / y
    z := x % y
    output := round(x)
    output := abs(x)
    output := count(collection_or_string)
    output := sum(array_or_set)
    output := product(array_or_set)
    output := max(array_or_set)
    output := min(array_or_set)
    output := sort(array_or_set)
    output := all(array_or_set)
    output := any(array_or_set)
}

arrays {
    output := array.concat(array, array)
    output := array.slice(array, startIndex, stopIndex)
}

sets {
    s3 := s1 & s2
    s3 := s1 | s2
    s3 := s1 - s2
    output := intersection(set[set])
    output := union(set[set])
}

objects {
    value := object.get(object, key, default)
    output := object.remove(object, keys)
    output := object.union(objectA, objectB)
    filtered := object.filter(object, keys)
    filtered := json.filter(object, paths)
    output := json.remove(object, paths)
}

strings {
    output := concat(delimiter, array_or_set)
    contains(string, search)
    endswith(string, search)
    output := format_int(number, base)
    output := indexof(string, search)
    output := lower(string)
    output := replace(string, old, new)
    output := strings.replace_n(patterns, string)
    output := split(string, delimiter)
    output := sprintf(string, values)
    startswith(string, search)
    output := substring(string, start, length)
    output := trim(string, cutset)
    output := trim_left(string, cutset)
    output := trim_prefix(string, prefix)
    output := trim_right(string, cutset)
    output := trim_suffix(string, suffix)
    output := trim_space(string)
    output := upper(string)
}

regex {
    re_match(pattern, value)
    output := regex.split(pattern, string)
    regex.globs_match(glob1, glob2)
    output := regex.template_match(pattern, string, delimiter_start, delimiter_end)
    output := regex.find_n(pattern, string, number)
    output := regex.find_all_string_submatch_n(pattern, string, number)
}

globFunc {
    output := glob.match(pattern, delimiters, match)
    output := glob.quote_meta(pattern)
    output := glob.match("*.github.com", [], "api.github.com")
    output := glob.match("*.github.com", [], "api.cdn.github.com")
    output := glob.match("*:github:com", [":"], "api:github:com")
    output := glob.match("api.**.com", [], "api.github.com")
    output := glob.match("api.**.com", [], "api.cdn.github.com")
    output := glob.match("?at", [], "cat")
    output := glob.match("?at", [], "at")
    output := glob.match("[abc]at", [], "bat")
    output := glob.match("[abc]at", [], "cat")
    output := glob.match("[abc]at", [], "lat")
    output := glob.match("[!abc]at", [], "cat")
    output := glob.match("[!abc]at", [], "lat")
    output := glob.match("[a-c]at", [], "cat")
    output := glob.match("[a-c]at", [], "lat")
    output := glob.match("[!a-c]at", [], "cat")
    output := glob.match("[!a-c]at", [], "lat")
    output := glob.match("{cat,bat,[fr]at}", [], "cat")
    output := glob.match("{cat,bat,[fr]at}", [], "bat")
    output := glob.match("{cat,bat,[fr]at}", [], "rat")
    output := glob.match("{cat,bat,[fr]at}", [], "at")
}

bitwise {
    z := bits.or(x, y)
    z := bits.and(x, y)
    z := bits.negate(x)
    z := bits.xor(x, y)
    z := bits.lsh(x, s)
    z := bits.rsh(x, s)
}

conversions {
    output := to_number(x)
}

unitsFunc {
    output := units.parse_bytes(x)
}

types {
    output := is_number(x)
    output := is_string(x)
    output := is_boolean(x)
    output := is_array(x)
    output := is_set(x)
    output := is_object(x)
    output := is_null(x)
    output := type_name(x)
}

encoding {
    output := base64.encode(x)
    output := base64.decode(string)
    output := base64url.encode(x)
    output := base64url.decode(string)
    output := urlquery.encode(string)
    output := urlquery.encode_object(object)
    output := urlquery.decode(string)
    output := json.marshal(x)
    output := json.unmarshal(string)
    output := yaml.marshal(x)
    output := yaml.unmarshal(string)
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
    output := io.jwt.verify_rs256(string, certificate)
    output := io.jwt.verify_rs384(string, certificate)
    output := io.jwt.verify_rs512(string, certificate)
    output := io.jwt.verify_ps256(string, certificate)
    output := io.jwt.verify_ps384(string, certificate)
    output := io.jwt.verify_ps512(string, certificate)
    output := io.jwt.verify_es256(string, certificate)
    output := io.jwt.verify_es384(string, certificate)
    output := io.jwt.verify_es512(string, certificate)
    output := io.jwt.verify_hs256(string, secret)
    output := io.jwt.verify_hs384(string, secret)
    output := io.jwt.verify_hs512(string, secret)
    output := io.jwt.decode(string)
    output := io.jwt.decode_verify(string, constraints)
}

timeFunc {
    output := time.now_ns()
    output := time.parse_ns(layout, value)
    output := time.parse_rfc3339_ns(value)
    output := time.parse_duration_ns(duration)
    output := time.date(ns)
    output := time.date([ns, tz])
    output := time.clock(ns)
    output := time.clock([ns, tz])
    day := time.weekday(ns)
    day := time.weekday([ns, tz])
    output := time.add_date(ns, years, months, days)
}

cryptography {
    output := crypto.x509.parse_certificates(string)
    output := crypto.md5(string)
    output := crypto.sha1(string)
    output := crypto.sha256(string)

}

graphs {
    walk(x, [path, value])
}

graphs2[entity_name] = edges {
  org_chart_data[entity_name]
  edges := {neighbor | org_chart_data[neighbor].owner == entity_name}
}

graphs2[entity_name] = access {
  org_chart_data[entity_name]
  reachable := graph.reachable(org_chart_graph, {entity_name})
  access := {item | reachable[k]; item := org_chart_data[k].access[_]}
}

httpFunc {
    response := http.send(request)
}

networking {
    net.cidr_contains_matches("1.1.1.0/24", "1.1.1.128")
    net.cidr_contains_matches(["1.1.1.0/24", "1.1.2.0/24"], "1.1.1.128")
    net.cidr_contains_matches([["1.1.0.0/16", "foo"], "1.1.2.0/24"], ["1.1.1.128", ["1.1.254.254", "bar"]])
    net.cidr_contains_matches({["1.1.0.0/16", "foo"], "1.1.2.0/24"}, {"x": "1.1.1.128", "y": ["1.1.254.254", "bar"]})
    net.cidr_intersects(cidr1, cidr2)
    net.cidr_contains(cidr, cidr_or_ip)
    net.cidr_expand(cidr)
}

uuidFunc {
    output := uuid.rfc4122(str)
}

regoFunc {
    output := rego.parse_module(filename, string)
}

opaFunc {
    output := opa.runtime()
}

debugging {
    trace(string)
}