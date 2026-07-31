# Tiger's Shinobi Universe

Tiger's Shinobi Universe is an early-development NeoForge mod for Minecraft
1.21.1. It adds persistent shinobi characters, clans, chakra, water walking,
wall running, enhanced jumping, and server-authoritative hand-sign inputs.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.244 or newer in the 21.1 line
- Java 21
- Gravity Changer 2.x for NeoForge

The Gravity Changer development JAR is currently stored in `libs/` so the
project can compile without an additional Maven repository. Players and
servers must still install the Gravity Changer mod separately.

## Development

Clone the repository and run:

```text
./gradlew build
```

On Windows, use `gradlew.bat build`. The generated mod JAR is written to
`build/libs/`.

Useful development tasks:

```text
./gradlew runClient
./gradlew runServer
./gradlew runGameTestServer
```

## Default controls

| Action | Key |
| --- | --- |
| Hand Sign 1 | R |
| Hand Sign 2 | F |
| Charge Chakra | C |
| Shinobi Menu | V |
| Wall Run | Unbound |
| Wall Run Modifier | Left Alt |

Bind Wall Run in Minecraft's Controls screen. Hold the Wall Run key and use a
block to select its surface; use the modifier while pressing Wall Run to
cancel.

## Project status

This project is under active development. Gameplay data and the network
protocol may change between builds. Please report reproducible problems in
the repository's issue tracker.
