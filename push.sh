#!/bin/bash

# Optional: commit message as argument, otherwise default message
COMMIT_MSG=${1:-"Auto-commit $(date +"%Y-%m-%d %H:%M:%S")"}

# Stage all changes
git add .

# Commit
git commit -m "$COMMIT_MSG"

# Push to main branch
git push -u origin main

echo "Changes committed and pushed!"
