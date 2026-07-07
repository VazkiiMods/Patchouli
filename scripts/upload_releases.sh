#!/usr/bin/env bash
set -euo pipefail

# Remove 'refs/tags/' from front
TAGNAME="${GIT_REF/#refs\/tags\/}"

# Remove 'release-' from front
VERSION="${TAGNAME/#release-}"
MC_VERSION=$(echo "${VERSION}" | cut -d '-' -f 1)
VERSION_TYPE=$(echo "${VERSION}" | cut -d '-' -f 3)
VERSION_TYPE="${VERSION_TYPE:-"release"}"

if [[ -v DRY_RUN ]];then
  echo "Dry run set to $DRY_RUN"
fi

function release_github() {
  if [[ -v DRY_RUN && "$DRY_RUN" == "true" ]];then
    GH_RELEASE_PAGE="http://example.com"
    return 0
  fi

  echo >&2 'Creating GitHub Release'

  local GH_RELEASE_RESPONSE
  GH_RELEASE_RESPONSE="$(gh api \
     --method POST \
     -H "Accept: application/vnd.github+json" \
     -H "X-GitHub-Api-Version: 2022-11-28" \
     /repos/VazkiiMods/Patchouli/releases \
     -f tag_name="${TAGNAME}")"
  GH_RELEASE_PAGE=$(echo "$GH_RELEASE_RESPONSE" | jq -r .html_url)

  echo >&2 'Uploading Fabric Jar and Signature to GitHub'
  gh release upload "${TAGNAME}" "${FABRIC_JAR}#Fabric Jar"
  gh release upload "${TAGNAME}" "${FABRIC_JAR}.asc#Fabric Signature"
  echo >&2 'Uploading NeoForge Jar and Signature to GitHub'
  gh release upload "${TAGNAME}" "${NEOFORGE_JAR}#NeoForge Jar"
  gh release upload "${TAGNAME}" "${NEOFORGE_JAR}.asc#NeoForge Signature"
}

function release_modrinth() {
  local MODRINTH_FABRIC_SPEC
  MODRINTH_FABRIC_SPEC=$(cat <<EOF
{
  "dependencies": [
    {
      "project_id": "P7dR8mSH",
      "dependency_type": "required"
    }
  ],
  "loaders": ["fabric", "quilt"],
  "featured": false,
  "project_id": "nU0bVIaL",
  "file_parts": [
    "jar"
  ],
  "primary_file": "jar"
}
EOF
            )

  MODRINTH_FABRIC_SPEC=$(echo "${MODRINTH_FABRIC_SPEC}" | \
                 jq --arg name "${VERSION}-fabric" \
                    --arg version "${VERSION}" \
                    --arg mcver "${MC_VERSION}" \
                    --arg changelog "${GH_RELEASE_PAGE}" \
                    --arg version_type "${VERSION_TYPE}" \
                    '.name=$ARGS.named.name | .version_number=$ARGS.named.version | .game_versions=[$ARGS.named.mcver] | .changelog=$ARGS.named.changelog | .version_type=$ARGS.named.version_type')

  if [[ -v DRY_RUN && "$DRY_RUN" == "true" ]];then
    echo "$MODRINTH_FABRIC_SPEC"
  else
    echo >&2 'Uploading Fabric Jar to Modrinth'
    curl 'https://api.modrinth.com/v2/version' \
       -H "Authorization: $MODRINTH_TOKEN" \
       -F "data=$MODRINTH_FABRIC_SPEC" \
       -F "jar=@${FABRIC_JAR}"
    # TODO modrinth doesn't allow asc files. Remember to readd "signature" to the spec when reenabling this. \ -F "signature=@${FABRIC_JAR}.asc"
  fi

  local MODRINTH_NEOFORGE_SPEC
  MODRINTH_NEOFORGE_SPEC=$(cat <<EOF
{
  "dependencies": [],
  "loaders": ["neoforge"],
  "featured": false,
  "project_id": "nU0bVIaL",
  "file_parts": [
    "jar"
  ],
  "primary_file": "jar"
}
EOF
             )

  MODRINTH_NEOFORGE_SPEC=$(echo "${MODRINTH_NEOFORGE_SPEC}" | \
                jq --arg name "${VERSION}-neoforge" \
                   --arg version "${VERSION}" \
                   --arg mcver "${MC_VERSION}" \
                   --arg changelog "${GH_RELEASE_PAGE}" \
                   --arg version_type "${VERSION_TYPE}" \
                   '.name=$ARGS.named.name | .version_number=$ARGS.named.version | .game_versions=[$ARGS.named.mcver] | .changelog=$ARGS.named.changelog | .version_type=$ARGS.named.version_type')

  if [[ -v DRY_RUN && "$DRY_RUN" == "true" ]];then
    echo "$MODRINTH_NEOFORGE_SPEC"
  else
    echo >&2 'Uploading NeoForge Jar to Modrinth'
    curl 'https://api.modrinth.com/v2/version' \
       -H "Authorization: $MODRINTH_TOKEN" \
       -F "data=$MODRINTH_NEOFORGE_SPEC" \
       -F "jar=@${NEOFORGE_JAR}"
    # TODO modrinth doesn't allow asc files. Remember to readd "signature" to the spec when reenabling this. \ -F "signature=@${FORGE_JAR}.asc"
  fi
}

function release_curseforge() {
  # Java versions, Loaders, and Environment tags are actually "game versions" (lmfao), as are real game versions.

  local CURSEFORGE_FABRIC_SPEC
  CURSEFORGE_FABRIC_SPEC=$(cat <<EOF
{
  "changelogType": "text",
  "relations": {
    "projects": [
      {
        "slug": "fabric-api",
        "type": "requiredDependency"
      }
    ]
  },
  "gameVersionNames": ["Client", "Server", "Fabric", "Quilt", "Java 25"]
}
EOF
              )

  CURSEFORGE_FABRIC_SPEC=$(echo "$CURSEFORGE_FABRIC_SPEC" | \
                 jq --arg changelog "$GH_RELEASE_PAGE" \
                    --arg mcver "${MC_VERSION}" \
                    --arg version_type "${VERSION_TYPE}" \
                    '.gameVersionNames += [$ARGS.named.mcver] | .releaseType=$ARGS.named.version_type | .changelog=$ARGS.named.changelog')

  if [[ -v DRY_RUN && "$DRY_RUN" == "true" ]];then
    echo "$CURSEFORGE_FABRIC_SPEC"
  else
    echo >&2 'Uploading Fabric Jar to CurseForge'
    curl 'https://minecraft.curseforge.com/api/projects/393236/upload-file' \
       -H "X-Api-Token: $CURSEFORGE_TOKEN" \
       -F "metadata=$CURSEFORGE_FABRIC_SPEC" \
       -F "file=@$FABRIC_JAR"
    # TODO: Upload the asc as an 'Additional file'
  fi

  local CURSEFORGE_NEOFORGE_SPEC
  CURSEFORGE_NEOFORGE_SPEC=$(cat <<EOF
{
    "changelogType": "text",
    "gameVersionNames": ["Client", "Server", "NeoForge", "Java 25"]
}
EOF
             )

  CURSEFORGE_NEOFORGE_SPEC=$(echo "$CURSEFORGE_NEOFORGE_SPEC" | \
                jq --arg changelog "$GH_RELEASE_PAGE" \
                   --arg mcver "${MC_VERSION}" \
                   --arg version_type "${VERSION_TYPE}" \
                   '.gameVersionNames += [$ARGS.named.mcver] | .releaseType=$ARGS.named.version_type | .changelog=$ARGS.named.changelog')

  if [[ -v DRY_RUN && "$DRY_RUN" == "true" ]];then
    echo "$CURSEFORGE_NEOFORGE_SPEC"
  else
    echo >&2 'Uploading NeoForge Jar to CurseForge'
    curl 'https://minecraft.curseforge.com/api/projects/306770/upload-file' \
       -H "X-Api-Token: $CURSEFORGE_TOKEN" \
       -F "metadata=$CURSEFORGE_NEOFORGE_SPEC" \
       -F "file=@$NEOFORGE_JAR"
    # TODO: Upload the asc as an 'Additional file'
  fi
}

release_github
release_modrinth
release_curseforge
