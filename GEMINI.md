# Lifesteal Plugin

This project is a Minecraft Paper plugin named **Lifesteal**, designed for version 1.21. It implements dimension locking mechanics and bans/modifies specific items and behaviors (like crystals and anchors) to balance gameplay, likely for a Lifesteal SMP server.

## Project Structure

*   **Language:** Java 21
*   **Build System:** Maven
*   **Target Platform:** Paper 1.21
*   **Main Class:** `hu.szatomi.lifesteal.Lifesteal`

## Key Features

### 1. Dimension Locking
The plugin restricts access to the **Nether** and **The End** dimensions until a configured real-world date.
*   **Manager:** `DimensionLockManager.java`
*   **Listener:** `DimensionListener.java`
*   **Configuration:** Dates and messages are set in `config.yml`.
    *   Format: `YYYY-MM-DD HH:mm:ss`

### 2. Gameplay Restrictions (`BannedItemsListener.java`)
The plugin modifies several vanilla behaviors to prevent "cheap" kills or griefing:
*   **Beds:** Players cannot enter beds in the Nether or End (prevents bed bombing).
*   **Respawn Anchors:** Right-clicking an anchor outside the Nether is cancelled (unless placing a block), preventing anchor explosions in the Overworld/End.
*   **End Crystals:**
    *   Crystal explosions do **not** destroy blocks (`EntityExplodeEvent` block list cleared).
    *   Crystals do **not** deal damage to entities (`EntityDamageByEntityEvent` cancelled).

## Configuration (`config.yml`)

The `config.yml` file controls dimension unlock times and messages.

```yaml
dimensions:
  nether:
    unlock-date: "2026-03-02 16:00:00"
    locked-message: "§cThe Nether is locked! Opens: ..."
  the_end:
    unlock-date: "2026-03-14 10:00:00"
    locked-message: "§cThe End is locked! Opens: ..."
```

## Commands

*   `/lifestealreload`: Reloads the configuration from disk.
    *   **Permission:** `lifesteal.admin`
    *   **Logic:** `ReloadCommand.java`

## Building and Running

### Prerequisites
*   Java Development Kit (JDK) 21
*   Maven

### Build Command
To build the plugin jar:
```bash
mvn clean package
```
The compiled artifact will be located at:
`target/lifesteal-1.0.jar`

### Installation
1.  Stop your Minecraft server.
2.  Place the generated `.jar` file into the server's `plugins/` directory.
3.  Start the server.

## Development

*   **Dependencies:** The project depends on `io.papermc.paper:paper-api` (version 1.21.11).
*   **Shading:** The `maven-shade-plugin` is configured to shade dependencies if necessary, though currently, the project mainly uses the provided Paper API.
