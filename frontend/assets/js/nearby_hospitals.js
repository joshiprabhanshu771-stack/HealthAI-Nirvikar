document.addEventListener("DOMContentLoaded", () => {

    // =====================================================
    // Existing HTML Elements
    // =====================================================

    const findHospitalsBtn =
        document.getElementById("findHospitalsBtn");

    const hospitalResults =
        document.getElementById("hospitalResults");

    const locationStatus =
        document.getElementById("locationStatus");

    const filterButtons =
        document.querySelectorAll(".filter-btn");


    // =====================================================
    // New Location Search Elements
    // =====================================================

    const useCurrentLocationBtn =
        document.getElementById("useCurrentLocationBtn");

    const searchLocationBtn =
        document.getElementById("searchLocationBtn");

    const locationSearch =
        document.getElementById("locationSearch");

    const selectedLocation =
        document.getElementById("selectedLocation");


    // =====================================================
    // Safety Check
    // =====================================================

    if (
        !hospitalResults ||
        !locationStatus
    ) {

        console.error(
            "Nearby healthcare elements are missing from HTML."
        );

        return;
    }


    // =====================================================
    // Default Search Radius
    // =====================================================

    const radius = 5;


    // =====================================================
    // Store All Healthcare Results
    // =====================================================

    let allHealthcareResults = [];


    // =====================================================
    // USER'S ACTUAL CURRENT LOCATION
    //
    // This is only used for Google Maps directions.
    // =====================================================

    let userLatitude = null;
    let userLongitude = null;


    // =====================================================
    // SEARCH LOCATION
    //
    // This is the location from which hospital distance
    // will be calculated.
    //
    // Example:
    // User = Delhi
    // Search = Indore
    //
    // searchLatitude/searchLongitude = Indore
    // =====================================================

    let searchLatitude = null;
    let searchLongitude = null;

    let searchLocationName = "";


    // =====================================================
    // Helper:
    // Set Search Location
    // =====================================================

    function setSearchLocation(
        latitude,
        longitude,
        locationName
    ) {

        searchLatitude =
            parseFloat(latitude);

        searchLongitude =
            parseFloat(longitude);

        searchLocationName =
            locationName || "Selected Location";


        console.log(
            "Search Location:",
            searchLocationName
        );

        console.log(
            "Search Latitude:",
            searchLatitude
        );

        console.log(
            "Search Longitude:",
            searchLongitude
        );


        if (selectedLocation) {

            selectedLocation.textContent =
                `📍 ${searchLocationName}`;

        }

    }


    // =====================================================
    // USE CURRENT LOCATION
    // =====================================================

    function useCurrentLocation() {

        if (!navigator.geolocation) {

            locationStatus.textContent =
                "Geolocation is not supported by your browser.";

            return;
        }


        locationStatus.textContent =
            "Getting your current location...";


        const button =
            useCurrentLocationBtn ||
            findHospitalsBtn;


        if (button) {

            button.disabled = true;

        }


        navigator.geolocation.getCurrentPosition(

            async (position) => {

                // =============================================
                // ACTUAL USER LOCATION
                // =============================================

                userLatitude =
                    position.coords.latitude;

                userLongitude =
                    position.coords.longitude;


                console.log(
                    "User Current Latitude:",
                    userLatitude
                );

                console.log(
                    "User Current Longitude:",
                    userLongitude
                );


                console.log(
                    "Location Accuracy:",
                    position.coords.accuracy,
                    "meters"
                );


                // =============================================
                // IMPORTANT
                //
                // When using current location:
                // Search location = Current location
                // =============================================

                setSearchLocation(
                    userLatitude,
                    userLongitude,
                    "Your Current Location"
                );


                locationStatus.textContent =
                    "Searching nearby healthcare services...";


                // =============================================
                // Call Backend
                // =============================================

                await findNearbyHealthcare(
                    searchLatitude,
                    searchLongitude,
                    radius
                );


                if (button) {

                    button.disabled = false;

                }

            },


            (error) => {

                console.error(
                    "Geolocation Error:",
                    error
                );


                let message =
                    "Unable to get your location.";


                switch (error.code) {

                    case error.PERMISSION_DENIED:

                        message =
                            "Location permission denied. Please allow location access.";

                        break;


                    case error.POSITION_UNAVAILABLE:

                        message =
                            "Your location is currently unavailable.";

                        break;


                    case error.TIMEOUT:

                        message =
                            "Location request timed out. Please try again.";

                        break;

                }


                locationStatus.textContent =
                    message;


                const button =
                    useCurrentLocationBtn ||
                    findHospitalsBtn;


                if (button) {

                    button.disabled = false;

                }

            },

            {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 0
            }

        );

    }


    // =====================================================
    // Existing "Find Hospitals" Button
    //
    // This button will now mean:
    // USE MY CURRENT LOCATION
    // =====================================================

    if (findHospitalsBtn) {

        findHospitalsBtn.addEventListener(
            "click",
            () => {

                useCurrentLocation();

            }
        );

    }


    // =====================================================
    // New "Use Current Location" Button
    // =====================================================

    if (useCurrentLocationBtn) {

        useCurrentLocationBtn.addEventListener(
            "click",
            () => {

                useCurrentLocation();

            }
        );

    }


    // =====================================================
    // SEARCH ANOTHER LOCATION
    // =====================================================

    async function searchAnotherLocation() {

        if (!locationSearch) {

            console.error(
                "locationSearch input not found."
            );

            return;

        }


        const query =
            locationSearch.value.trim();


        if (!query) {

            locationStatus.textContent =
                "Please enter a location.";

            return;

        }


        locationStatus.textContent =
            "Finding location...";


        if (searchLocationBtn) {

            searchLocationBtn.disabled =
                true;

        }


        try {

            // =============================================
            // OpenStreetMap Nominatim Geocoding
            // =============================================

            const url =
                `https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=${encodeURIComponent(query)}`;


            console.log(
                "Geocoding URL:",
                url
            );


            const response =
                await fetch(url);


            if (!response.ok) {

                throw new Error(
                    `Geocoding failed: ${response.status}`
                );

            }


            const results =
                await response.json();


            console.log(
                "Geocoding Results:",
                results
            );


            if (
                !results ||
                results.length === 0
            ) {

                throw new Error(
                    "Location not found."
                );

            }


            const result =
                results[0];


            const latitude =
                parseFloat(result.lat);


            const longitude =
                parseFloat(result.lon);


            // =============================================
            // Validate Coordinates
            // =============================================

            if (
                !Number.isFinite(latitude) ||
                !Number.isFinite(longitude)
            ) {

                throw new Error(
                    "Invalid coordinates received."
                );

            }


            // =============================================
            // SET SEARCH LOCATION
            //
            // IMPORTANT:
            // We DO NOT change userLatitude/userLongitude.
            //
            // Example:
            // User is in Delhi
            // Search is Indore
            //
            // userLatitude = Delhi
            // searchLatitude = Indore
            // =============================================

            setSearchLocation(
                latitude,
                longitude,
                result.display_name || query
            );


            locationStatus.textContent =
                "Searching healthcare services...";


            // =============================================
            // Call Backend Using SEARCH LOCATION
            // =============================================

            await findNearbyHealthcare(
                searchLatitude,
                searchLongitude,
                radius
            );

        }

        catch (error) {

            console.error(
                "Location Search Error:",
                error
            );


            locationStatus.textContent =
                "Location could not be found. Please try another location.";

        }

        finally {

            if (searchLocationBtn) {

                searchLocationBtn.disabled =
                    false;

            }

        }

    }


    // =====================================================
    // Search Location Button
    // =====================================================

    if (searchLocationBtn) {

        searchLocationBtn.addEventListener(
            "click",
            () => {

                searchAnotherLocation();

            }
        );

    }


    // =====================================================
    // Press ENTER in Location Search
    // =====================================================

    if (locationSearch) {

        locationSearch.addEventListener(
            "keydown",
            (event) => {

                if (
                    event.key === "Enter"
                ) {

                    event.preventDefault();

                    searchAnotherLocation();

                }

            }
        );

    }


    // =====================================================
    // Fetch Nearby Healthcare
    // =====================================================

    async function findNearbyHealthcare(
        latitude,
        longitude,
        radius
    ) {

        try {

            // =============================================
            // Validate Search Location
            // =============================================

            if (
                !Number.isFinite(
                    parseFloat(latitude)
                ) ||
                !Number.isFinite(
                    parseFloat(longitude)
                )
            ) {

                throw new Error(
                    "Invalid search location."
                );

            }


            // =============================================
            // Backend API
            // =============================================

           const apiUrl = `http://localhost:8080/api/hospitals/nearby?lat=${latitude}&lon=${longitude}&radius=${radius}`;


            console.log(
                "Healthcare API URL:",
                apiUrl
            );


            const response =
                await fetch(apiUrl);


            if (!response.ok) {

                throw new Error(
                    `HTTP error: ${response.status}`
                );

            }


            const data =
                await response.json();


            console.log(
                "Healthcare API Response:",
                data
            );


            if (!data.success) {

                throw new Error(
                    "Healthcare search failed."
                );

            }


            // =============================================
            // Store Results
            // =============================================

            allHealthcareResults =
                Array.isArray(data.hospitals)
                    ? data.hospitals
                    : [];


            // =============================================
            // Calculate Distance
            //
            // VERY IMPORTANT:
            //
            // Distance is calculated from SEARCH LOCATION.
            //
            // NOT from user's actual location.
            // =============================================

            allHealthcareResults =
                allHealthcareResults.map(
                    (healthcare) => {

                        const facilityLatitude =
                            parseFloat(
                                healthcare.latitude
                            );


                        const facilityLongitude =
                            parseFloat(
                                healthcare.longitude
                            );


                        // =====================================
                        // Check Facility Coordinates
                        // =====================================

                        if (
                            Number.isFinite(
                                facilityLatitude
                            ) &&
                            Number.isFinite(
                                facilityLongitude
                            )
                        ) {

                            const distance =
                                calculateDistance(
                                    latitude,
                                    longitude,
                                    facilityLatitude,
                                    facilityLongitude
                                );


                            return {

                                ...healthcare,

                                distanceKm:
                                    distance

                            };

                        }


                        // =====================================
                        // Coordinates Missing
                        // =====================================

                        return {

                            ...healthcare,

                            distanceKm:
                                null

                        };

                    }
                );


            // =============================================
            // Sort By Distance
            // =============================================

            allHealthcareResults.sort(
                (a, b) => {

                    if (
                        a.distanceKm === null
                    ) {

                        return 1;

                    }


                    if (
                        b.distanceKm === null
                    ) {

                        return -1;

                    }


                    return (
                        a.distanceKm -
                        b.distanceKm
                    );

                }
            );


            console.log(
                "Healthcare Results With Distance:",
                allHealthcareResults
            );


            // =============================================
            // Display Results
            // =============================================

            displayHealthcare(
                allHealthcareResults
            );


            // =============================================
            // Status Message
            // =============================================

            locationStatus.textContent =
                `${allHealthcareResults.length} healthcare services found within ${radius} km. Source: ${data.source || "Unknown"}`;

        }

        catch (error) {

            console.error(
                "Healthcare API Error:",
                error
            );


            locationStatus.textContent =
                "Unable to load nearby healthcare services.";


            hospitalResults.innerHTML = `

                <div class="location-card">

                    <h3>

                        <i class="fa-solid fa-triangle-exclamation"></i>

                        Something went wrong

                    </h3>

                    <p class="location-address">

                        Could not load nearby healthcare services.

                        Please try again.

                    </p>

                </div>

            `;

        }

    }


    // =====================================================
    // Haversine Distance Calculation
    // =====================================================

    function calculateDistance(
        lat1,
        lon1,
        lat2,
        lon2
    ) {

        const R = 6371;


        const dLat =
            toRadians(
                lat2 - lat1
            );


        const dLon =
            toRadians(
                lon2 - lon1
            );


        const latitude1 =
            toRadians(lat1);


        const latitude2 =
            toRadians(lat2);


        const a =
            Math.sin(dLat / 2) *
            Math.sin(dLat / 2) +

            Math.cos(latitude1) *
            Math.cos(latitude2) *

            Math.sin(dLon / 2) *
            Math.sin(dLon / 2);


        const c =
            2 *
            Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
            );


        return R * c;

    }


    // =====================================================
    // Convert Degrees To Radians
    // =====================================================

    function toRadians(
        degrees
    ) {

        return (
            degrees *
            Math.PI /
            180
        );

    }


    // =====================================================
    // Format Distance
    // =====================================================

    function formatDistance(
        distanceKm
    ) {

        if (
            distanceKm === null ||
            distanceKm === undefined ||
            !Number.isFinite(
                distanceKm
            )
        ) {

            return "Distance unavailable";

        }


        if (
            distanceKm < 1
        ) {

            const meters =
                Math.round(
                    distanceKm * 1000
                );


            return `${meters} m away`;

        }


        return `${distanceKm.toFixed(1)} km away`;

    }


    // =====================================================
    // Display Healthcare
    // =====================================================

    function displayHealthcare(
        healthcareList
    ) {

        hospitalResults.innerHTML = "";


        if (
            !healthcareList ||
            healthcareList.length === 0
        ) {

            hospitalResults.innerHTML = `

                <div class="location-card">

                    <h3>

                        <i class="fa-solid fa-circle-info"></i>

                        No Healthcare Services Found

                    </h3>

                    <p class="location-address">

                        No hospitals, clinics, pharmacies
                        or laboratories were found within
                        the selected radius.

                    </p>

                </div>

            `;

            return;

        }


        healthcareList.forEach(
            (healthcare) => {

                const card =
                    document.createElement(
                        "div"
                    );


                card.className =
                    "location-card";


                card.innerHTML =
                    createHealthcareCard(
                        healthcare
                    );


                hospitalResults.appendChild(
                    card
                );

            }
        );

    }


    // =====================================================
    // Create Healthcare Card
    // =====================================================

    function createHealthcareCard(
        healthcare
    ) {

        const type =
            healthcare.hospitalType ||
            "Healthcare";


        const typeInfo =
            getTypeInfo(type);


        const distance =
            formatDistance(
                healthcare.distanceKm
            );


        // =============================================
        // Facility Coordinates
        // =============================================

        const facilityLatitude =
            parseFloat(
                healthcare.latitude
            );


        const facilityLongitude =
            parseFloat(
                healthcare.longitude
            );


        let directionsButton = "";


        // =============================================
        // Google Maps Directions
        // =============================================

        if (
            Number.isFinite(
                facilityLatitude
            ) &&
            Number.isFinite(
                facilityLongitude
            )
        ) {

            // -----------------------------------------
            // Directions Origin
            //
            // Priority:
            //
            // 1. Actual user's current location
            // 2. Search location
            //
            // Example:
            // User in Delhi
            // Search Indore
            //
            // Distance = Indore -> Hospital
            // Directions = Delhi -> Hospital
            // -----------------------------------------

            let originLatitude =
                searchLatitude;

            let originLongitude =
                searchLongitude;


            if (
                Number.isFinite(
                    userLatitude
                ) &&
                Number.isFinite(
                    userLongitude
                )
            ) {

                originLatitude =
                    userLatitude;

                originLongitude =
                    userLongitude;

            }


            const directionsUrl =
                `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(originLatitude + "," + originLongitude)}&destination=${encodeURIComponent(facilityLatitude + "," + facilityLongitude)}`;


            directionsButton = `

                <div style="margin-top: 15px;">

                    <a
                        href="${escapeAttribute(
                            directionsUrl
                        )}"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="btn btn-primary btn-block">

                        <i class="fa-solid fa-route"></i>

                        Get Directions

                    </a>

                </div>

            `;

        }


        // =============================================
        // Healthcare Card HTML
        // =============================================

        return `

            <div class="location-header">

                <span
                    class="type-badge ${typeInfo.badgeClass}">

                    <i class="${typeInfo.icon}"></i>

                    ${escapeHtml(type)}

                </span>

            </div>


            <h3>

                ${escapeHtml(
                    healthcare.name ||
                    "Unnamed Healthcare Facility"
                )}

            </h3>


            <p class="location-address">

                <i class="fa-solid fa-location-dot"></i>

                ${escapeHtml(
                    healthcare.address ||
                    "Address not available"
                )}

            </p>


            ${
                healthcare.phone
                ? `

                    <p class="location-address">

                        <i class="fa-solid fa-phone"></i>

                        ${escapeHtml(
                            healthcare.phone
                        )}

                    </p>

                `
                : ""
            }


            ${
                healthcare.openingHours
                ? `

                    <p class="location-address">

                        <i class="fa-solid fa-clock"></i>

                        ${escapeHtml(
                            healthcare.openingHours
                        )}

                    </p>

                `
                : ""
            }


            <!-- ===================================== -->
            <!-- Distance From SEARCH LOCATION -->
            <!-- ===================================== -->

            <div class="location-meta">

                <span>

                    <i class="fa-solid fa-location-dot text-teal"></i>

                    ${escapeHtml(distance)}

                </span>

            </div>


            ${
                healthcare.website
                ? `

                    <div style="margin-top: 15px;">

                        <a
                            href="${escapeAttribute(
                                healthcare.website
                            )}"
                            target="_blank"
                            rel="noopener noreferrer"
                            class="btn btn-secondary btn-block">

                            <i class="fa-solid fa-globe"></i>

                            Visit Website

                        </a>

                    </div>

                `
                : ""
            }


            ${directionsButton}

        `;

    }


    // =====================================================
    // Healthcare Type Information
    // =====================================================

    function getTypeInfo(
        type
    ) {

        const normalizedType =
            String(type)
                .toLowerCase()
                .trim();


        switch (
            normalizedType
        ) {

            case "hospital":

                return {

                    icon:
                        "fa-solid fa-hospital",

                    badgeClass:
                        "badge-hospital"

                };


            case "clinic":

                return {

                    icon:
                        "fa-solid fa-stethoscope",

                    badgeClass:
                        "badge-clinic"

                };


            case "pharmacy":

                return {

                    icon:
                        "fa-solid fa-prescription-bottle-medical",

                    badgeClass:
                        "badge-pharmacy"

                };


            case "laboratory":

                return {

                    icon:
                        "fa-solid fa-flask",

                    badgeClass:
                        "badge-lab"

                };


            case "blood bank":

                return {

                    icon:
                        "fa-solid fa-droplet",

                    badgeClass:
                        "badge-blood"

                };


            default:

                return {

                    icon:
                        "fa-solid fa-house-medical",

                    badgeClass:
                        "badge-hospital"

                };

        }

    }


    // =====================================================
    // Filter Buttons
    // =====================================================

    filterButtons.forEach(
        (button) => {

            button.addEventListener(
                "click",
                () => {

                    // =========================================
                    // Remove Active
                    // =========================================

                    filterButtons.forEach(
                        (btn) => {

                            btn.classList.remove(
                                "active"
                            );

                        }
                    );


                    // =========================================
                    // Add Active
                    // =========================================

                    button.classList.add(
                        "active"
                    );


                    const buttonText =
                        button.textContent
                            .trim()
                            .toLowerCase();


                    console.log(
                        "Selected Filter:",
                        buttonText
                    );


                    let filteredResults;


                    // =========================================
                    // All Services
                    // =========================================

                    if (
                        buttonText.includes(
                            "all services"
                        )
                    ) {

                        filteredResults =
                            allHealthcareResults;

                    }


                    // =========================================
                    // Hospitals
                    // =========================================

                    else if (
                        buttonText.includes(
                            "hospitals"
                        )
                    ) {

                        filteredResults =
                            filterByType(
                                "hospital"
                            );

                    }


                    // =========================================
                    // Clinics
                    // =========================================

                    else if (
                        buttonText.includes(
                            "clinics"
                        )
                    ) {

                        filteredResults =
                            filterByType(
                                "clinic"
                            );

                    }


                    // =========================================
                    // Pharmacies
                    // =========================================

                    else if (
                        buttonText.includes(
                            "pharmacies"
                        )
                    ) {

                        filteredResults =
                            filterByType(
                                "pharmacy"
                            );

                    }


                    // =========================================
                    // Laboratories
                    // =========================================

                    else if (
                        buttonText.includes(
                            "laboratories"
                        )
                    ) {

                        filteredResults =
                            filterByType(
                                "laboratory"
                            );

                    }


                    // =========================================
                    // Blood Banks
                    // =========================================

                    else if (
                        buttonText.includes(
                            "blood banks"
                        )
                    ) {

                        filteredResults =
                            filterByType(
                                "blood bank"
                            );

                    }


                    else {

                        filteredResults =
                            allHealthcareResults;

                    }


                    displayHealthcare(
                        filteredResults
                    );

                }
            );

        }
    );


    // =====================================================
    // Filter By Type
    // =====================================================

    function filterByType(
        type
    ) {

        return allHealthcareResults.filter(
            (healthcare) => {

                const healthcareType =
                    String(
                        healthcare.hospitalType ||
                        ""
                    )
                    .toLowerCase()
                    .trim();


                return (
                    healthcareType ===
                    type
                );

            }
        );

    }


    // =====================================================
    // Escape HTML
    // =====================================================

    function escapeHtml(
        value
    ) {

        const div =
            document.createElement(
                "div"
            );


        div.textContent =
            String(value);


        return div.innerHTML;

    }


    // =====================================================
    // Escape Attribute
    // =====================================================

    function escapeAttribute(
        value
    ) {

        return String(value)

            .replace(
                /&/g,
                "&amp;"
            )

            .replace(
                /"/g,
                "&quot;"
            )

            .replace(
                /'/g,
                "&#39;"
            )

            .replace(
                /</g,
                "&lt;"
            )

            .replace(
                />/g,
                "&gt;"
            );

    }

});