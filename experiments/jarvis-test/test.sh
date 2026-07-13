#!/bin/bash

PROMPT=$(
cat \
system/identity.md \
system/personality.md \
system/rules.md \
system/capabilities.md \
system/limitations.md \
memory/people.md \
memory/home.md \
memory/projects.md \
memory/preferences.md \
context/conversation.md \
context/devices.md \
context/status.md

echo
echo "#############################"
echo "PERGUNTA DO USUÁRIO"
echo "#############################"
echo
echo "$1"
)

echo
echo "==============================="
echo "PROMPT ENVIADO"
echo "==============================="
echo
echo "$PROMPT"
echo

curl http://localhost:11434/api/generate \
-d "$(jq -n \
--arg prompt "$PROMPT" \
'{
model:"jarvis",
prompt:$prompt,
stream:false
}')"
