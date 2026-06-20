#!/bin/bash
# ============================================
# BichoHub — Script de inicialização (Linux/Mac)
# Uso: ./run.sh up                    (sobe containers)
#      ./run.sh down                  (derruba containers)
#      ./run.sh build                 (reconstrói a imagem)
#      ./run.sh make-admin <id>       (promove usuário <id> a admin)
#      ./run.sh bot-on                (ativa o bot manualmente)
#      ./run.sh bot-off               (desativa o bot)
# ============================================

ENV_FILE=".env.development"

run_up() {
    echo "============================================"
    echo "  BichoHub — Iniciando ambiente de dev"
    echo "============================================"

    if ! docker info > /dev/null 2>&1; then
        echo "[ERRO] Docker nao esta rodando."
        echo "  Inicie o Docker e tente novamente."
        exit 1
    fi

    echo "[1/2] Iniciando containers (build automatico se necessario)..."
    docker compose --env-file "$ENV_FILE" up -d

    if [ $? -ne 0 ]; then
        echo "[ERRO] Falha ao subir os containers."
        echo "  Verifique o Dockerfile e as dependencias."
        exit 1
    fi

    echo "[2/2] Acompanhando logs (pressione Ctrl+C para parar)..."
    echo ""
    echo "  Server: http://localhost:6969"
    echo "  Status: http://localhost:6969/api/status"
    echo ""
    echo "  Qualquer alteracao no codigo reinicia automaticamente."
    echo "============================================"
    echo ""

    docker compose --env-file "$ENV_FILE" logs -f
}

run_down() {
    echo "============================================"
    echo "  BichoHub — Parando ambiente de dev"
    echo "============================================"

    docker compose --env-file "$ENV_FILE" down -v --remove-orphans
    echo ""
    echo "  Containers removidos."
    echo "============================================"
}

run_build() {
    echo "============================================"
    echo "  BichoHub — Reconstruindo imagem"
    echo "============================================"

    docker compose --env-file "$ENV_FILE" build --no-cache
    echo ""
    echo "  Imagem reconstruida."
    echo "============================================"
}

run_make_admin() {
    if [ -z "$2" ]; then
        echo "[ERRO] Use: $0 make-admin <ID do usuario>"
        exit 1
    fi

    ID="$2"
    CPF=$(printf "%011d" "$ID")

    echo "============================================"
    echo "  Promovendo usuario $ID a administrador..."
    echo "============================================"

    docker compose --env-file "$ENV_FILE" exec -T db_postgres psql -U postgres -d bichohub -c "INSERT INTO administrador (usuario_id, cpf) VALUES ($ID, '$CPF') ON CONFLICT (usuario_id) DO NOTHING;"

    if [ $? -eq 0 ]; then
        echo ""
        echo "  Usuario $ID agora e administrador!"
        echo "============================================"
    else
        echo "[ERRO] Falha ao promover usuario."
    fi
}

run_bot_on() {
    echo "Ativando bot do WhatsApp..."
    docker compose --env-file "$ENV_FILE" exec -T backend curl -s -X POST http://localhost:6969/api/whatsapp/ativar
    echo ""
    echo "  Bot ativado!"
}

run_bot_off() {
    echo "Desativando bot do WhatsApp..."
    docker compose --env-file "$ENV_FILE" exec -T backend curl -s -X POST http://localhost:6969/api/whatsapp/desativar
    echo ""
    echo "  Bot desativado."
}

case "${1:-up}" in
    up)          run_up ;;
    down)        run_down ;;
    build)       run_build ;;
    make-admin)  run_make_admin "$@" ;;
    bot-on)      run_bot_on ;;
    bot-off)     run_bot_off ;;
    *)
        echo "Uso: $0 {up|down|build|make-admin <id>|bot-on|bot-off}"
        exit 1
        ;;
esac
