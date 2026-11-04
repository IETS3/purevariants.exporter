#!/usr/bin/env python3
"""Writes a Markdown test and coverage summary (for $GITHUB_STEP_SUMMARY) from
the Surefire XML reports and the JaCoCo aggregate report. Standard library only.

Usage: test-summary.py [repo-root]   (Markdown goes to stdout)
"""
import csv
import glob
import os
import sys
import xml.etree.ElementTree as ET

root = sys.argv[1] if len(sys.argv) > 1 else "."

suites = []
for f in sorted(glob.glob(os.path.join(root, "tests/**/target/surefire-reports/TEST-*.xml"), recursive=True)):
    s = ET.parse(f).getroot()
    suites.append(s)

total = failed = errors = skipped = 0
time_s = 0.0
rows = []
problems = []
for s in suites:
    t, fl, er, sk = (int(s.get(k, 0)) for k in ("tests", "failures", "errors", "skipped"))
    total, failed, errors, skipped = total + t, failed + fl, errors + er, skipped + sk
    time_s += float(s.get("time", 0))
    name = s.get("name", "?").rsplit(".", 1)[-1]
    status = "❌" if fl or er else ("⏭️" if t and sk == t else "✅")
    rows.append(f"| {status} | {name} | {t} | {fl + er} | {sk} | {float(s.get('time', 0)):.2f} s |")
    for tc in s.iter("testcase"):
        for kind in ("failure", "error"):
            e = tc.find(kind)
            if e is not None:
                msg = (e.get("message") or "").strip().splitlines()
                problems.append((f"{tc.get('classname', '').rsplit('.', 1)[-1]}.{tc.get('name')}",
                                 msg[0] if msg else kind))

print("## Unit tests\n")
if not suites:
    print("⚠️ No Surefire reports found.\n")
else:
    passed = total - failed - errors - skipped
    icon = "❌" if failed or errors else "✅"
    print(f"{icon} **{passed} passed**, {failed + errors} failed, {skipped} skipped "
          f"of {total} tests in {time_s:.1f} s\n")
    print("| | Test class | Tests | Failed | Skipped | Time |\n|---|---|---:|---:|---:|---:|")
    print("\n".join(rows))
    if problems:
        print("\n### Failures\n")
        for name, msg in problems:
            print(f"- `{name}`: {msg}")
    print()

print("> Integration tests need a pure::variants license server and are not run here.\n")

print("## Coverage (JaCoCo, unit tests only)\n")
print("> Only code that runs without a pure::variants license is measured. The exporters and "
      "the facade need the skipped integration tests, so their 0 % does not mean they are untested.\n")
csv_files = glob.glob(os.path.join(root, "tests/**/target/site/jacoco-aggregate/jacoco.csv"), recursive=True)
if not csv_files:
    print("⚠️ No JaCoCo report found.")
else:
    counters = {"INSTRUCTION": [0, 0], "BRANCH": [0, 0], "LINE": [0, 0], "METHOD": [0, 0]}
    by_pkg = {}
    with open(csv_files[0], newline="") as fh:
        for r in csv.DictReader(fh):
            pkg = by_pkg.setdefault(r["PACKAGE"], {k: [0, 0] for k in counters})
            for k in counters:
                missed, covered = int(r[f"{k}_MISSED"]), int(r[f"{k}_COVERED"])
                counters[k][0] += missed
                counters[k][1] += covered
                pkg[k][0] += missed
                pkg[k][1] += covered

    def pct(c):
        m, v = c
        return f"{100 * v / (m + v):.1f} %" if m + v else "–"

    print("| Package | Instructions | Branches | Lines | Methods |\n|---|---:|---:|---:|---:|")
    for name, p in sorted(by_pkg.items()):
        print(f"| `{name}` | {pct(p['INSTRUCTION'])} | {pct(p['BRANCH'])} | {pct(p['LINE'])} | {pct(p['METHOD'])} |")
    print(f"| **Total** | **{pct(counters['INSTRUCTION'])}** | **{pct(counters['BRANCH'])}** "
          f"| **{pct(counters['LINE'])}** | **{pct(counters['METHOD'])}** |")
    print("\nThe full HTML report is in the `coverage-report` artifact.")

sys.exit(1 if failed or errors else 0)
