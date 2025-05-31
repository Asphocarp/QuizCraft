- [ ] 0 cannot change hotkey! & cannot block changing weapon!
  - for now, simply let the user remove original keybinding of 1234

- [ ] 1 notebook or wrong ans; forve review - maybe introduce fancy SOTA algo (FSRS)

- [ ] 1 add channel for ping (default channel string is "default") (only forward pings to this channel (teammates subscript to this channel))
- [ ] 1 screen border indicator, with margin?
- [ ] 1 stats for memo words (like anki?)
- [ ] 1 config for penalty


- [ ] 2 render a hang sword ? (beside the ping)
- [ ] 2 maybe - 紫甲,蓝甲,白甲 - 每次碎甲直到没甲
- [ ] 3 cool&hard: tas版本，PvP时比谁回答的快
- [ ] 3 allow pause?
- [ ] 2 可能只hook on death比较合理，甚至可以让他复活得更强
- [ ] 2 polish mod config page: dropdown selection for sounds, color picker for highlight color
- [ ] 2 more dramatic effect for wrong answer, the mob become huge?

drama:
- support PvP!!!
- cursor emotion
1. (break, attack) * (wrong, correct)
2. config
3. using FSRS (Free Spaced Repetition Scheduler) (same with Anki/momo)

key diff:
- PvP, multi-player support
- anki support (not just dict)
- better algo?


done:
- [x] 0 simple doc, publish to modrinth
- [x] 1 think: make it persistent? sync periodically? X
- [x] 0 shield-broken sound
- [x] 0 re-naming: QuizCraft
- [x] select book (and other global server-side config, like highlight color)
- [x] 1 current MVP .damage() needs some fix. it use current weapon and can be blocked by current shield.
- [x] many ping coexist (at most 1 for each entity) (and match the nearest to the screen center as the active one)
- [x] correct/wrong, send the answer, feedback (good/bad)
- [x] 0 no more cancel / ping func (of PingSystem) - cancel is only for debug
- [x] fix render block ping as same size and normal color
- [x] shuffle the data / prepare the data & toefl data; gen quiz
- [x] 0 render quiz on screen, indicator points to the mob?
- [x] fix, sync logic. first ping may not be applied. log in&out, activePings is clear but client still has it.
- [x] 1 shield-block-sound; fix shield blocking cause it to not even knockback
- [x] 1 key down, highlight current focused option; key up, select