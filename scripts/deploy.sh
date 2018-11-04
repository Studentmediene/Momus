#!/bin/bash

set -e # This will abort the script if any command outputs errors

MASTER_BRANCH="master"
DEVELOP_BRANCH="develop"

function usage {
    echo "Usage: deploy.sh [NEW VERSION]"
}

function error {
    echo "Error: $1"
    echo
    usage
    exit
}

function confirm_deploy {
    echo -e "Deploying Momus. This will:"
    echo -e "\tUpdate version to $1"
    echo -e "\tMerge the following commits into $MASTER_BRANCH:"
    git log --oneline --decorate --no-merges $MASTER_BRANCH..$DEVELOP_BRANCH
    echo
    echo -e "\tYou will be prompted to write a tag message, which should describe changes made"
    read -p "Are you sure you want to deploy? [y/N] " -r
    if ! [[ $REPLY =~ ^[Yy]$ ]]
    then
        echo "Deploy aborted!"
        exit
    fi
}

function get_old_version {
    DIR=$(dirname "$0")
    cd $DIR/../
    OLD_VER=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
    echo $OLD_VER
}

function update_from_remote {
    echo "Pulling from remote to ensure local branches are up-to-date"
    git checkout $MASTER_BRANCH
    git pull
    git checkout $DEVELOP_BRANCH
    git pull
}
function check_no_git_changes {
    DIR=$(dirname "$0")
    cd $DIR/../
    if [ -n "$(git status --porcelain)" ]; then
        error "Commit all changes before deploy"
    fi
}

function check_correct_branch {
    BRANCH=$(git rev-parse --abbrev-ref HEAD)
    if ! [[ $BRANCH == $DEVELOP_BRANCH ]]; then
        error "Not on $DEVELOP_BRANCH branch"
    fi
}

function check_argument_set {
    if [[ ! "$1" ]]; then
        error "Version argument not set"
    fi
}

function check_newer_than_existing {
    OLD_VER=$(get_old_version)

    if [[ $1 == $OLD_VER ]]; then
        error "Please bump the version number"
    fi

    OLD_VER_ARR=(${OLD_VER//./ })
    NEW_VER_ARR=(${1//./ })
    for i in $(seq 0 2); do
        if [[ ${NEW_VER_ARR[$i]} < ${OLD_VER_ARR[$i]} ]]; then
            error "New version must be > $OLD_VER"
        fi
    done
}

function check_argument_pattern {
    if ! [[ $1 =~ ^([0-9]+\.)([0-9]+\.)([0-9]+)$ ]]; then
        error "Invalid version number input. Should be x.y.z"
    fi
}

function set_pom_version {
    echo "Updating pom.xml"
    DIR=$(dirname "$0")
    cd $DIR/../
    mvn versions:set -DnewVersion=$1 -DgenerateBackupPoms=false -q
}

function set_npm_version {
    echo "Updating package.json"
    DIR=$(dirname "$0")
    cd $DIR/../
    npm version $1 --no-git-tag-version > "/dev/null"
}

NEW_VER=$1

# Check that input is valid
check_argument_set $NEW_VER
check_argument_pattern $NEW_VER
check_newer_than_existing $NEW_VER

# Check git state is valid
check_correct_branch
check_no_git_changes
update_from_remote

# Confirm that user wants to do this
confirm_deploy $NEW_VER

set_pom_version $NEW_VER
set_npm_version $NEW_VER

# Make sure we are in correct dir
DIR=$(dirname "$0")
cd $DIR/../


# Commit changes to version
git add package.json pom.xml
git commit -m "Bump versions to $NEW_VER"

# Merge into master and create tag
git checkout $MASTER_BRANCH
git merge $DEVELOP_BRANCH --no-ff --no-edit
git tag -a $NEW_VER
git push --all --tags
