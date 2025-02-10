#!/bin/bash

#
# Copyright (c) 2025 Proton AG
# This file is part of Proton AG and Proton Authenticator.
#
# Proton Authenticator is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Proton Authenticator is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Proton Authenticator.  If not, see <https://www.gnu.org/licenses/>.
#

if [[ $# -eq 0 ]]; then
    echo "Invalid usage. ${0} VERSION"
    exit 1
fi

VERSION=$1

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
REPO_ROOT=$(echo "${SCRIPT_DIR}" | sed 's:scripts::g')

VERSION_CODE=$("${REPO_ROOT}/scripts/versionCode.sh" "${VERSION}")

echo -e "versionName=${VERSION}\nversionCode=${VERSION_CODE}" > metadata/fdroid_version.txt

touch "metadata/en-US/changelogs/${VERSION_CODE}.txt"
