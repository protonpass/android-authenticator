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

MAJOR=$(echo "$VERSION" | cut -d. -f1)
MINOR=$(echo "$VERSION" | cut -d. -f2)
PATCH=$(echo "$VERSION" | cut -d. -f3)

MAJOR_CODE=$((MAJOR * 10000000))
MINOR_CODE=$((MINOR * 100000))
PATCH_CODE=$((PATCH * 1000))

VERSION_CODE=$((MAJOR_CODE + MINOR_CODE + PATCH_CODE))

echo -n "${VERSION_CODE}"
