document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector(".bmi-form");

    if (!form) {
        console.error("BMI form not found.");
        return;
    }

    const heightInput = document.getElementById("height");
    const weightInput = document.getElementById("weight");

    const resultNumber = document.querySelector(".score-num");
    const resultCategory = document.querySelector(".result-category");
    const resultMessage = document.querySelector(".score-details p");

    const calculateButton = form.querySelector("button[type='submit']");

    // Backend BMI category -> Frontend display name
    const categoryLabels = {
        UNDERWEIGHT: "Underweight",
        HEALTHY_WEIGHT: "Normal Weight",
        OVERWEIGHT: "Overweight",
        OBESITY: "Obese",

        // Extra formats for safety
        "UNDER WEIGHT": "Underweight",
        "HEALTHY WEIGHT": "Normal Weight",
        "NORMAL_WEIGHT": "Normal Weight",
        "NORMAL": "Normal Weight",
        "OVER WEIGHT": "Overweight",
        "OBESE": "Obese"
    };

    form.addEventListener("submit", async (event) => {

        event.preventDefault();

        // Get logged-in user's ID
        const userId = localStorage.getItem("healthAIUserId");

        // Get height and weight
        const height = Number(heightInput.value);
        const weight = Number(weightInput.value);

        // Check login
        if (!userId) {
            alert("User session not found. Please login again.");
            return;
        }

        // Validate height
        if (!height || height <= 0) {
            alert("Please enter a valid height.");
            heightInput.focus();
            return;
        }

        // Validate weight
        if (!weight || weight <= 0) {
            alert("Please enter a valid weight.");
            weightInput.focus();
            return;
        }

        // Save original button text
        const oldButtonText = calculateButton.innerHTML;

        calculateButton.disabled = true;
        calculateButton.innerHTML = "Calculating...";

        try {

            const response = await fetch("/api/bmi/calculate", {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    userId: Number(userId),
                    height: height,
                    heightUnit: "CM",
                    weight: weight,
                    weightUnit: "KG"
                })
            });

            const data = await response.json();

            console.log("BMI API response:", data);

            if (!response.ok) {
                throw new Error(
                    data.message || "BMI calculation failed."
                );
            }

            // ------------------------------------------------
            // UPDATE BMI NUMBER
            // ------------------------------------------------

            if (resultNumber && data.bmi != null) {
                resultNumber.textContent =
                    Number(data.bmi).toFixed(2);
            }

            // ------------------------------------------------
            // GET CATEGORY
            // ------------------------------------------------

            const rawCategory =
                data.category ??
                data.bmiCategory ??
                data.categoryName ??
                "";

            console.log("Raw BMI category:", rawCategory);

            // Normalize category
            const normalizedCategory =
                String(rawCategory)
                    .trim()
                    .toUpperCase()
                    .replace(/-/g, "_");

            // Convert backend category to readable text
            let category =
                categoryLabels[normalizedCategory];

            // ------------------------------------------------
            // FALLBACK:
            // If backend does not send category but BMI exists,
            // determine the display category from BMI value.
            // ------------------------------------------------

            if (!category && data.bmi != null) {

                const bmiValue = Number(data.bmi);

                if (bmiValue < 18.5) {
                    category = "Underweight";
                } else if (bmiValue < 25) {
                    category = "Normal Weight";
                } else if (bmiValue < 30) {
                    category = "Overweight";
                } else {
                    category = "Obese";
                }
            }

            if (!category) {
                category = "Unknown";
            }

            console.log("Displayed BMI category:", category);

            // ------------------------------------------------
            // UPDATE CATEGORY
            // ------------------------------------------------

            if (resultCategory) {

                resultCategory.innerHTML =
                    '<i class="fa-solid fa-circle-check"></i> ' +
                    category;

                // Remove old category color classes
                resultCategory.classList.remove(
                    "text-green",
                    "text-orange",
                    "text-red"
                );

                // Apply appropriate color
                if (category === "Normal Weight") {
                    resultCategory.classList.add("text-green");
                } else if (
                    category === "Underweight" ||
                    category === "Overweight"
                ) {
                    resultCategory.classList.add("text-orange");
                } else if (category === "Obese") {
                    resultCategory.classList.add("text-red");
                }
            }

            // ------------------------------------------------
            // UPDATE MESSAGE
            // ------------------------------------------------

            if (resultMessage) {

                resultMessage.textContent =
                    data.message ||
                    data.disclaimer ||
                    "BMI calculated successfully.";
            }

        } catch (error) {

            console.error("BMI calculation error:", error);

            alert(
                error.message ||
                "Unable to calculate BMI."
            );

        } finally {

            calculateButton.disabled = false;
            calculateButton.innerHTML = oldButtonText;
        }
    });
});