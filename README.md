NerdFlags
=========
WorldGuard custom flags for the Reddit Public Minecraft servers.

Available flags:

| Flag Name             | Type                  | Default Value |
|-----------------------|-----------------------|---------------|
| date                  | String                | *N/A*         |
| created-by            | String                | *N/A*         |
| entry-commands        | String                | *N/A*         |
| weather               | State                 | DENY          |
| compass               | State                 | ALLOW         |
| teleport-entry        | State                 | ALLOW         |
| force-gamemode        | GameMode              | null          |
| allow-drops           | State                 | ALLOW         |
| allow-mob-drops       | State                 | ALLOW         |
| player-mob-damage     | Set&lt;EntityType&gt; | {}            |
| nether-portal         | State                 | ALLOW         |
| end-portal            | State                 | ALLOW         |
| snowball-firefight    | State                 | DENY          |
| warp                  | Location              | *N/A*         |
| use-dispenser         | State                 | ALLOW         |
| use-note-block        | State                 | DENY          |
| use-workbench         | State                 | ALLOW         |
| use-door              | State                 | ALLOW         |
| use-lever             | State                 | ALLOW         |
| use-pressure-plate    | State                 | ALLOW         |
| use-button            | State                 | ALLOW         |
| use-jukebox           | State                 | ALLOW         |
| use-repeater          | State                 | DENY          |
| use-trap-door         | State                 | ALLOW         |
| use-fence-gate        | State                 | ALLOW         |
| use-brewing-stand     | State                 | ALLOW         |
| use-cauldron          | State                 | ALLOW         |
| use-enchantment-table | State                 | ALLOW         |
| use-ender-chest       | State                 | ALLOW         |
| use-tripwire          | State                 | ALLOW         |
| use-beacon            | State                 | ALLOW         |
| use-anvil             | State                 | ALLOW         |
| use-comparator        | State                 | DENY          |
| use-hopper            | State                 | ALLOW         |
| use-dropper           | State                 | ALLOW         |


Building
--------
The `WGRegionEvents` plugin doesn't currently have an active Maven repository.

So it is compiled and installed in the local repository as follows:
```
git clone https://github.com/mewin/WorldGuard-Region-Events
cd WorldGuard-Region-Events
mvn install
```
