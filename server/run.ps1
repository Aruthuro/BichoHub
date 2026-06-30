# ============================================
# BichoHub - Script de inicializacao (Windows)
# Uso: .\run.ps1 up                    (sobe containers)
#      .\run.ps1 down                  (derruba containers)
#      .\run.ps1 build                 (reconstroi a imagem)
#      .\run.ps1 make-admin <id>       (promove usuario <id> a admin)
#      .\run.ps1 bot-on                (ativa o bot manualmente)
#      .\run.ps1 bot-off               (desativa o bot)
# ============================================

param(
    [Parameter(Position = 0)]
    [string]$Comando = "up",

    [Parameter(Position = 1)]
    [string]$Param1 = ""
)

$ENV_FILE = ".env.development"

function RunUp {
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host "  BichoHub - Iniciando ambiente de dev" -ForegroundColor Cyan
    Write-Host "============================================" -ForegroundColor Cyan

    try {
        docker info *>$null
        if (-not $?) { throw "Docker nao esta respondendo" }
    } catch {
        Write-Host "[ERRO] Docker nao esta rodando." -ForegroundColor Red
        Write-Host "  Inicie o Docker Desktop e tente novamente." -ForegroundColor Yellow
        exit 1
    }

    Write-Host "[1/2] Iniciando containers (build automatico se necessario)..." -ForegroundColor Yellow
    docker compose --env-file "$ENV_FILE" up -d

    if (-not $?) {
        Write-Host "[ERRO] Falha ao subir os containers." -ForegroundColor Red
        Write-Host "  Verifique o Dockerfile e as dependencias." -ForegroundColor Yellow
        exit 1
    }

    Write-Host "[2/2] Acompanhando logs (pressione Ctrl+C para parar)..." -ForegroundColor Green
    Write-Host ""
    Write-Host "  Server: http://localhost:6969" -ForegroundColor Cyan
    Write-Host "  Status: http://localhost:6969/api/status" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  Qualquer alteracao no codigo reinicia automaticamente." -ForegroundColor Cyan
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host ""

    docker compose --env-file "$ENV_FILE" logs -f
}

function RunDown {
    Write-Host "============================================" -ForegroundColor Yellow
    Write-Host "  BichoHub - Parando ambiente de dev" -ForegroundColor Yellow
    Write-Host "============================================" -ForegroundColor Yellow

    docker compose --env-file "$ENV_FILE" down -v --remove-orphans
    Write-Host ""
    Write-Host "  Containers removidos." -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Yellow
}

function RunBuild {
    Write-Host "============================================" -ForegroundColor Yellow
    Write-Host "  BichoHub - Reconstruindo imagem" -ForegroundColor Yellow
    Write-Host "============================================" -ForegroundColor Yellow

    docker compose --env-file "$ENV_FILE" build --no-cache
    Write-Host ""
    Write-Host "  Imagem reconstruida." -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Yellow
}

function RunMakeAdmin {
    if ([string]::IsNullOrWhiteSpace($Param1)) {
        Write-Host "[ERRO] Use: .\run.ps1 make-admin <ID do usuario>" -ForegroundColor Red
        exit 1
    }

    $ID = $Param1

    Write-Host "============================================" -ForegroundColor Yellow
    Write-Host "  Promovendo usuario $ID a administrador..." -ForegroundColor Yellow
    Write-Host "============================================" -ForegroundColor Yellow

    # Insere diretamente no banco (via API seria chicken-egg: precisa de admin pra criar admin)
    $CPF = $ID.ToString().PadLeft(11, '0')
    docker compose --env-file "$ENV_FILE" exec -T db_postgres psql -U postgres -d bichohub -c "INSERT INTO administrador (usuario_id) VALUES ($ID) ON CONFLICT (usuario_id) DO NOTHING;"

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "  Usuario $ID agora e administrador!" -ForegroundColor Green
        Write-Host "============================================" -ForegroundColor Yellow
    } else {
        Write-Host "[ERRO] Falha ao promover usuario." -ForegroundColor Red
    }
}

function RunBotOn {
    Write-Host "Ativando bot do WhatsApp..." -ForegroundColor Yellow
    docker compose --env-file "$ENV_FILE" exec -T backend curl -s -X POST http://localhost:6969/api/whatsapp/ativar
    Write-Host ""
    Write-Host "  Bot ativado! Agora responde mensagens." -ForegroundColor Green
}

function RunBotOff {
    Write-Host "Desativando bot do WhatsApp..." -ForegroundColor Yellow
    docker compose --env-file "$ENV_FILE" exec -T backend curl -s -X POST http://localhost:6969/api/whatsapp/desativar
    Write-Host ""
    Write-Host "  Bot desativado." -ForegroundColor Green
}

switch ($Comando) {
    "up"         { RunUp }
    "down"       { RunDown }
    "build"      { RunBuild }
    "make-admin" { RunMakeAdmin }
    "bot-on"     { RunBotOn }
    "bot-off"    { RunBotOff }
    default {
        Write-Host "Uso: .\run.ps1 {up|down|build|make-admin <id>|bot-on|bot-off}" -ForegroundColor Yellow
    }
}
