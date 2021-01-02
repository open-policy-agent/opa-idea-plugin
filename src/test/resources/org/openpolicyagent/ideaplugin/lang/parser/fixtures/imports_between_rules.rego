package test

import data.policy
import data.security.policy as p

default allow = false
allow {
    true
}

import data.domain
import data.otherdomain as od

rule_1 {
    1 == 1
}

import data.something