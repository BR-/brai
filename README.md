# BRAI

BRAI is a very weak Terran AI for StarCraft: Brood War.

## Strategy

### Current

* Build order:
	* Initial build order:
		*  9 - Depot
		* 14 - Command Center
		* 15 - Barracks
		* 16 - Refinery
		* 16 - Depot
		* 20 - Bunkers (three on each ramp)
		* 22 - Barracks
	* Builds 25 SCVs.
	* Constantly builds marines.
	* Constantly builds barracks.
	* Builds supply depots when close to supply cap.
* When it hits 100 idle marines, it a-moves 75 of them to the enemy base.
* The 11th SCV is used as a scout. The bot does very little with the information.

### Planned

(roughly in order)

* Better end-game behavior: it can currently fail to finish off opponents.
* Engineering Bay upgrades.
* More expansions.
* Building units other than marines.
* Better scouting:
	* Don't suicide scouts.
	* Use the information gained.
* Economy estimation.
* Combat micro.

## Code

* `brai`
	* `combat`
		* `CombatController`
			* Handles all military actions.
	* `scouting`
		* `EnemyUnit`
			* Represents a unit that might be hidden in the fog of war.
		* `ScoutController`
			* Takes an SCV and sends it to possible base locations.
	* `strategy`
		* `StrategyController`
			* Controls all build order and production.
	* `ui`
		* `ScreenController`
			* Moves the screen to what is currently happening.
	* `workers`
		* `carpentry`
			* `CarpentryController`
				* Finds places to build structures and assigns SCVs to do so.
			* `PlannedUnit`
				* Represents a structure that doesn't exist yet.
			* `RepairController`
				* Assigns SCVs to repair damaged structures.
		* `GasController`
			* Keeps 3 SCVs on each refinery.
		* `MiningContoller`
			* Sends idle SCVs to mine the closest mineral field.
		* `WorkerContoller`
			* Prevents game loss by pausing LUL.
	* `BetterBWListener`
		* Makes sure the bot prints exceptions.
	* `Bot`
		* Dispatches events to the various controllers.
	* `Main`
		* Restarts StarCraft.
	* `Util`
		* Various methods.