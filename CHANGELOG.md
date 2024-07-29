## Major Note ⚠️
This is an **alpha** build of PlayerEX.
Therefore, you should take precaution when playing.
While I have personally tested the mod multiple times with several mods,
there is a possibility of any kind of issue.

## Additions ☕
- Rewrote the entire PlayerEX mod.
- Added a new UI screen with owo-lib (thanks guys) (that is extendable, more details in the future). Default keybinding is `-` (minus).
  - **WE ARE FREE FROM SINGLE, LONG-DEBOUNCED INPUTS!** feel free to hammer at that button or put in a specific amount that is within range.
  - Tooltips have been added to specific areas in the UI. Discover these as you play.
  - For those eager, see `PlayerEXMenuRegistry`.
- Redesigned config with owo-lib (thanks guys x2)
- Redid Brigadier commands, more info on that in the future. For now note that you can access it still by using `/playerex`.
- Diminishing attributes are currently managed by its provided `smoothness` from DataAttributes.
  - In order to change smoothness and other values, open up the config using `/owo-config data_attributes` or accessing through ModMenu.

- Improved general stability of the mod.
- This took a lot of blood, sweat and tears to make. But there might be issues! Certain things need to be ironed out (primarily the diminishing factor, and making DataAttributes ui easier to work with).
- Report issues of game-breaking proportion, or supposedly incorrect values to our issues page.

## Changes ⚙️


## Removals 🚫
- Removed `reach-entity-attribute` due to issues. Due to the nature of reach being hard-coded in this version, it's relatively challenging to gauge. A substitute will be present on the UI in later version(s).