## Changes 🌽
- Fixed out-of-bounds exception when registering a menu.
- Renamed `rootComponent` in `MenuComponent` -> `screenRoot` to refer to the actual screens root.
  - If you plan on using this component, know that you can access the functions/members you need directly from the object itself.