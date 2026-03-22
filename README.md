# ⚔️ PlusArena

Plugin de Arena PvP FFA (Free For All) para **Spigot 1.21.4**.

Leve, otimizado e sem bugs de duplicação de itens.

---

## 📦 Instalação

1. Baixe o `PlusArena.jar` em [Releases](../../releases)
2. Coloque na pasta `plugins/` do seu servidor
3. Reinicie o servidor
4. Configure com os comandos abaixo

---

## ⚙️ Configuração Inicial

```
1. /arena wand          → pegue a wand para definir a região
2. /arenasetspawn       → defina o spawn de entrada (dentro da arena)
3. /arenasetrespawn     → defina o respawn de saída/morte (lobby/hub)
```

---

## 🕹️ Comandos

| Comando | Descrição | Permissão |
|---|---|---|
| `/arena` | Entra na arena PvP | `plusarena.use` |
| `/arena sair` | Sai da arena (cooldown configurável) | `plusarena.use` |
| `/arena wand` | Recebe a Wand para definir a região | `plusarena.admin` |
| `/arenasetspawn` | Define o spawn de entrada da arena | `plusarena.admin` |
| `/arenasetrespawn` | Define o respawn de saída/morte | `plusarena.admin` |

---

## 🔑 Permissões

| Permissão | Descrição | Padrão |
|---|---|---|
| `plusarena.use` | Usar a arena | Todos |
| `plusarena.admin` | Configurar a arena | OP |

---

## 📝 config.yml

```yaml
# Cooldown do /arena sair (em segundos)
sair-cooldown: 10

# Impedir jogadores de sair da região da arena
impedir-saida: true

# Comandos permitidos dentro da arena
comandos-permitidos:
  - "arena sair"
  - "arena"

# Kit recebido ao entrar (totalmente configurável)
kit:
  helmet:
    material: DIAMOND_HELMET
    enchantments:
      PROTECTION: 4
      UNBREAKING: 3
  # ... (chestplate, leggings, boots, items)
```

> O `config.yml` completo é gerado automaticamente ao iniciar o plugin pela primeira vez.

---

## ✅ Funcionalidades

- ⚔️ PvP livre FFA (todos contra todos)
- 🎒 Inventário obrigatoriamente vazio para entrar
- 🧰 Kit configurável (encantamentos, itens, slots)
- 💀 Ao morrer: drops cancelados, inventário limpo, teleporte ao respawn
- 🚪 `/arena sair` com cooldown configurável
- 🚧 Bloqueio de comandos dentro da arena (whitelist configurável)
- 📐 Região definida por POS1/POS2 via Wand (Machado de Ouro)
- 🔒 Impede saída da região (opcional)
- 🔌 Desconexão dentro da arena tratada corretamente
- 🪶 Zero dependências externas — apenas Spigot API

---

## 📌 Versão

| Campo | Valor |
|---|---|
| Versão | 1.0.0 |
| API | Spigot 1.21.4 |
| Java | 21 |
| Dependências | Nenhuma |

---

## 📄 Licença

MIT License — veja [LICENSE](LICENSE) para detalhes.
