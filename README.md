# 🌍🧬 Darwin World
A simulation enabling the observation of evolutionary processes in a virtual world.

## 📖 Project Description
**Darwin World** is a simulation where animals, plants, and environmental variables interact within a dynamic ecosystem. The primary goal is to observe natural selection in real-time. Animals traverse the map, consume resources, reproduce, and evolve.

The project visually represents the "survival of the fittest" principle, showing how specific genotypes adapt and propagate, with an optional twist of predator-prey dynamics.

## 🗝️ Key Elements

### 1. 🌍 The World
The simulation takes place on a rectangular grid. It features a central, lush "jungle" (equator) where plants grow more frequently, surrounded by harsher steppes.

### 2. 🐇 The Animals
Animals are driven by the need to gather energy to survive.
* **Movement:** They navigate the map based on their genetic sequence.
* **Energy:** Gained by eating plants (or hunting) and lost through daily existence and reproduction.
* **Predator/Victim Variant:** An optional game mode where predators can hunt and kill weaker victims to absorb their energy.
* **Lifecycle:** Animals that run out of energy die. Strong ones live to pass on their traits.

### 3. 🧬 Genotypes & Evolution
Every animal possesses a unique Genotype that determines its movement.
* **Inheritance:** Offspring inherit a mix of genes, proportional to the energy levels of their parents (stronger parents pass on more genes).
* **Mutation:** Configurable random mutations occur during reproduction, introducing new behaviors.

### 4. 🔄 The Simulation Cycle
The engine runs daily cycles in distinct phases:
1. **Cleanup:** Dead animals are removed from the map.
2. **Movement:** Animals move to new fields.
3. **Hunting:** Predators attack weaker animals (if the variant is active).
4. **Feeding:** Animals consume plants to gain energy.
5. **Reproduction:** Well-fed animals reproduce.
6. **Environment:** New plants grow.

### 5. 📊 Live Tracking & Data
The simulation offers live tracking of specific animals (energy, descendants, age) and automatically exports daily global statistics to CSV files.

## 🛠️ Technologies
This project is built using:
* **Java:** Core logic, genetics, and multithreaded simulation engine (`ExecutorService`).
* **JavaFX:** Used to create the graphical user interface (GUI) and FXML views for real-time visualization and configuration.