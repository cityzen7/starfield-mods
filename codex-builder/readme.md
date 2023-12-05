# Codex Builder

Scan all planets, flora, and fauna.

Possibly export to starfield wiki?

Generate data for The Eye

May need to teleport to each world to get scanned info

Print to plain text should be fine. Can post process to turn into javascript

Layout
```
OnGameLoad
Add Quest or Magic to player?
OnScan - update Progress?
```

Possible help?
```
ObjectReference
Event OnScanned()

ActiveMagicEffect
Event OnPlayerScannedObject(ObjectReference akScannedRef)

Actor
Event OnPlayerScannedObject(ObjectReference akScannedRef)

```