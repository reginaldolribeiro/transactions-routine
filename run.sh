#!/bin/bash

# A simple script to manage the application lifecycle using Docker Compose.
#
# USAGE:
#   ./run.sh [command]
#
# COMMANDS:
#   up      - Starts the application and database. Builds the app image if it doesn't exist. (Default)
#   down    - Stops and removes all services.
#   build   - Forces a rebuild of the application's Docker image.
#   test    - Runs the application's unit and integration tests in a container.
#   logs    - Tails the logs from the application service.
#   help    - Shows this help message.

# --- Colors for output ---
C_RESET='\033[0m'
C_RED='\033[0;31m'
C_GREEN='\033[0;32m'
C_BLUE='\033[0;34m'

# --- Global Variables ---
COMPOSE_CMD=""
COMPOSE_FILE="docker-compose.yaml"

# --- Helper Functions ---
function print_info() {
    echo -e "${C_BLUE}INFO: $1${C_RESET}"
}

function print_error() {
    echo -e "${C_RED}ERROR: $1${C_RESET}"
}

function find_compose_command() {
    if command -v docker &> /dev/null && docker compose version &> /dev/null; then
        COMPOSE_CMD="docker compose"
    elif command -v docker-compose &> /dev/null; then
        COMPOSE_CMD="docker-compose"
    else
        print_error "Neither 'docker compose' nor 'docker-compose' could be found. Please install Docker."
        exit 1
    fi
    print_info "Using '$COMPOSE_CMD' with file '$COMPOSE_FILE'."
}

# --- Main Commands ---
function build() {
    print_info "Building the application Docker image..."
    $COMPOSE_CMD -f $COMPOSE_FILE build app
}

function up() {
    print_info "Starting up services (database and application)..."
    # -d runs services in the background
    $COMPOSE_CMD -f $COMPOSE_FILE up --build -d
    print_info "Application should be running. Tailing logs (press Ctrl+C to exit)..."
    logs
}

function down() {
    print_info "Stopping and removing all services..."
    $COMPOSE_CMD -f $COMPOSE_FILE down
    print_info "Services stopped."
}

function test() {
    print_info "Running tests in a dedicated container..."
    # We run the tests in a one-off container based on the 'app' service definition.
    # The `--rm` flag ensures the container is removed after the tests complete.
    $COMPOSE_CMD -f $COMPOSE_FILE run --rm app ./mvnw test
}

function logs() {
    print_info "Following logs for the 'app' service..."
    $COMPOSE_CMD -f $COMPOSE_FILE logs -f app
}

function help() {
    # Extracts the usage and commands from the script's header comments.
    grep '^#' "$0" | cut -c3-
}

# --- Script Entrypoint ---
function main() {
    # Find the correct docker compose command
    find_compose_command

    # Parse command
    COMMAND="$1"
    case "$COMMAND" in
        build)
            build
            ;;
        up)
            up
            ;;
        down)
            down
            ;;
        test)
            test
            ;;
        logs)
            logs
            ;;
        help)
            help
            ;;
        "")
            # Default action if no command is provided
            up
            ;;
        *)
            print_error "Unknown command: $COMMAND"
            help
            exit 1
            ;;
    esac
}

# Execute the main function with all script arguments
main "$@"
