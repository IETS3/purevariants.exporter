#!/bin/bash
#
# Sets the version of all POMs, manifests, features, the update site category, the
# configuration POM and the exporter version written into exported files.
# Does not commit anything; prints a proposed commit message on success.

set -uo pipefail

script_name=$0
cd "$(dirname "$0")"

usage() {
	echo "Usage: $script_name <semantic_version, e.g. 1.2.3>"
}

fail() {
	echo
	echo "ERROR: $1"
	echo "The version bump is incomplete. Do NOT commit; inspect 'git status' / 'git diff'"
	echo "and revert the modified files with 'git checkout -- <file>' if necessary."
	exit 1
}

if [[ $# -eq 1 && ( $1 == "--help" || $1 == "-h" ) ]]; then
	usage
	exit 0
fi

if [[ $# -ne 1 || ! $1 =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
	usage
	exit 1
fi

version=$1
pom_version="$version-SNAPSHOT"

config_pom=releng/de.itemis.purevariants.exporter.configuration/pom.xml
exporter_java=bundles/de.itemis.purevariants.exporter.exporter/src/de/itemis/purevariants/exporter/exporter/PureVariantExporter.java
config_artifact='<artifactId>de\.itemis\.purevariants\.exporter\.configuration</artifactId>\s*<version>'

old_pom_version=$(perl -0ne "print \$1 if m|$config_artifact([^<]*)</version>|" "$config_pom")
[[ -n $old_pom_version ]] || fail "Could not determine the current version from $config_pom."
old_version=${old_pom_version%-SNAPSHOT}
if [[ $old_pom_version == "$pom_version" ]]; then
	echo "The version is already $pom_version, nothing to do."
	exit 1
fi

echo "Bumping version from $old_pom_version to $pom_version"

# Update POMs, MANIFEST.MF, feature.xml and category.xml files of all modules
mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion="$pom_version" -Dtycho.mode=maven \
	|| fail "Maven (tycho-versions-plugin:set-version) failed."

# The configuration POM is a parent, not a module, so Tycho does not update it,
# nor the reference to it in the root POM.
perl -0pi -e "s|($config_artifact)[^<]*(</version>)|\${1}$pom_version\${2}|g" "$config_pom" pom.xml \
	|| fail "Updating the configuration POM version failed."

perl -pi -e "s|(EXPORTER_VERSION = \")[^\"]*(\")|\${1}$version\${2}|" "$exporter_java" \
	|| fail "Updating EXPORTER_VERSION failed."

# Verify that no file still refers to the old version
leftovers=$(grep -rlE --exclude-dir=target --exclude-dir=.git --include=pom.xml --include=MANIFEST.MF --include=feature.xml --include=category.xml \
	"${old_pom_version//./\\.}|${old_version//./\\.}\.qualifier" . 2>/dev/null)
[[ -z $leftovers ]] || fail "These files still refer to the old version $old_pom_version:"$'\n'"$leftovers"

grep -q "EXPORTER_VERSION = \"$version\"" "$exporter_java" \
	|| fail "EXPORTER_VERSION in $exporter_java was not set to $version."

echo
echo "SUCCESS: The version was bumped to $pom_version."
echo "Review the changes with 'git diff' and, if they look fine, commit them, e.g.:"
echo
echo "    git commit -am \"Bump version to $version\""
