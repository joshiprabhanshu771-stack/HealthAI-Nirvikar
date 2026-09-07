package com.healthai.service;

import com.healthai.entity.Hospital;
import com.healthai.entity.HospitalSearchCache;
import com.healthai.repository.HospitalRepository;
import com.healthai.repository.HospitalSearchCacheRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalSearchCacheRepository cacheRepository;
    private final OverpassService overpassService;

    @Value("${hospital.cache.expiry.days:7}")
    private int cacheExpiryDays;

    @Value("${hospital.search.max-radius.km:50}")
    private double maxRadiusKm;

    public HospitalService(
            HospitalRepository hospitalRepository,
            HospitalSearchCacheRepository cacheRepository,
            OverpassService overpassService) {

        this.hospitalRepository = hospitalRepository;
        this.cacheRepository = cacheRepository;
        this.overpassService = overpassService;
    }

    @Transactional
    public HospitalSearchResponse findNearbyHospitals(
            double latitude,
            double longitude,
            double radiusKm) {

        validateCoordinates(latitude, longitude);
        validateRadius(radiusKm);

        LocalDateTime now = LocalDateTime.now();

        /*
         * STEP 1:
         * Check whether we already have valid cached data
         * for this location and radius.
         */
        List<HospitalSearchCache> validCaches =
                cacheRepository.findByExpiresAtAfter(now);

        HospitalSearchCache matchingCache = validCaches.stream()
                .filter(cache -> isLocationCovered(
                        cache,
                        latitude,
                        longitude,
                        radiusKm
                ))
                .max(Comparator.comparing(
                        HospitalSearchCache::getFetchedAt
                ))
                .orElse(null);

        /*
         * STEP 2:
         * If cache exists, return hospitals from DATABASE.
         */
        if (matchingCache != null) {

            List<Hospital> hospitals =
                    findHospitalsWithinRadius(
                            latitude,
                            longitude,
                            radiusKm
                    );

            return new HospitalSearchResponse(
                    true,
                    "DATABASE",
                    hospitals.size(),
                    hospitals
            );
        }

        /*
         * STEP 3:
         * No valid cache found.
         * Call Overpass API.
         */
        List<Hospital> fetchedHospitals =
                overpassService.fetchHospitals(
                        latitude,
                        longitude,
                        radiusKm
                );

        /*
         * STEP 4:
         * Save/update hospitals in MySQL.
         */
        List<Hospital> savedHospitals = new ArrayList<>();

        for (Hospital hospital : fetchedHospitals) {

            Hospital existing =
                    hospitalRepository
                            .findByOsmId(hospital.getOsmId())
                            .orElse(null);

            if (existing != null) {

                existing.setName(hospital.getName());
                existing.setAddress(hospital.getAddress());
                existing.setLatitude(hospital.getLatitude());
                existing.setLongitude(hospital.getLongitude());
                existing.setPhone(hospital.getPhone());
                existing.setWebsite(hospital.getWebsite());
                existing.setHospitalType(
                        hospital.getHospitalType()
                );
                existing.setOpeningHours(
                        hospital.getOpeningHours()
                );
                existing.setSource(hospital.getSource());

                savedHospitals.add(
                        hospitalRepository.save(existing)
                );

            } else {

                savedHospitals.add(
                        hospitalRepository.save(hospital)
                );
            }
        }

        /*
         * STEP 5:
         * Create cache entry.
         */
        HospitalSearchCache cache =
                new HospitalSearchCache();

        cache.setLatitude(latitude);
        cache.setLongitude(longitude);
        cache.setRadiusKm(radiusKm);
        cache.setFetchedAt(now);
        cache.setExpiresAt(
                now.plusDays(cacheExpiryDays)
        );

        cacheRepository.save(cache);

        /*
         * STEP 6:
         * Return fresh data.
         */
        return new HospitalSearchResponse(
                true,
                "OVERPASS_API",
                savedHospitals.size(),
                savedHospitals
        );
    }

    private boolean isLocationCovered(
            HospitalSearchCache cache,
            double latitude,
            double longitude,
            double radiusKm) {

        double distanceKm = calculateDistance(
                latitude,
                longitude,
                cache.getLatitude(),
                cache.getLongitude()
        );

        return distanceKm + radiusKm <= cache.getRadiusKm();
    }

    private List<Hospital> findHospitalsWithinRadius(
            double latitude,
            double longitude,
            double radiusKm) {

        List<Hospital> allHospitals =
                hospitalRepository.findAll();

        return allHospitals.stream()
                .filter(hospital -> {

                    double distance = calculateDistance(
                            latitude,
                            longitude,
                            hospital.getLatitude(),
                            hospital.getLongitude()
                    );

                    return distance <= radiusKm;
                })
                .toList();
    }

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double latDistance =
                Math.toRadians(lat2 - lat1);

        double lonDistance =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2)
                * Math.sin(latDistance / 2)
                +
                Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return EARTH_RADIUS_KM * c;
    }

    private void validateCoordinates(
            double latitude,
            double longitude) {

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    "Invalid latitude"
            );
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    "Invalid longitude"
            );
        }
    }

    private void validateRadius(double radiusKm) {

        if (radiusKm <= 0 || radiusKm > maxRadiusKm) {
            throw new IllegalArgumentException(
                    "Radius must be between 0 and "
                    + maxRadiusKm + " km"
            );
        }
    }

    public record HospitalSearchResponse(
            boolean success,
            String source,
            int count,
            List<Hospital> hospitals
    ) {
    }
}