package car.controller;

import car.bean.CarResponseDTO;
import car.entity.Car;
import car.entity.CarImages;
import car.service.CarService;
import car.service.CarMachineLearningService;
import car.service.CarRegistrationService;
import driverLicense.service.ObjectStorageService;
import login.dto.LoginDTO;
import login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins= {"http://localhost:3000", "https://dongwoossltest.shop"})
@RequestMapping(path = "/api")
public class CarRegistrationController {
    @Autowired
    private CarService carRegistrationService;
    @Autowired
    private ObjectStorageService objectStorageService;

    @Autowired
    private CarService carService;

    @Autowired
    CarMachineLearningService carMachineLearningService;

    @Autowired
    LoginService loginService;

    @PostMapping(path = "/users/{userId}/cars", consumes = {"multipart/form-data"})
    public ResponseEntity<Car> createCar(@RequestPart("car") Car car,
                                         @RequestPart("images") List<MultipartFile> images,
                                         @PathVariable("userId") String userId) {
        System.out.println("CarController start");
        // userId와 User 엔티티 연결..
        Optional<LoginDTO> byId = loginService.findById(userId);
        car.setUser(byId.get());

        System.out.println(car.toString());

        CarImages carImages = new CarImages();

        // car images uuid 값들을 저장
        List<String> carImagesUUID = new ArrayList<>();
        for(MultipartFile image: images){
            String uploadFileName = objectStorageService.uploadFile("cars/" , image);
            carImagesUUID.add(uploadFileName);
            System.out.println("uploadFileName = " + uploadFileName);
        }
        carRegistrationService.saveCarImages(carImages, carImagesUUID);

        car.setCarImages(carImages);
        Car savedCar = carRegistrationService.saveCar(car);     // 자동차 저장

        return ResponseEntity.ok(savedCar);
    }

    // 해당 유저의 등록된 자동차 목록을 가져오는 api
    @GetMapping(path = "/users/{userId}/cars")
    public ResponseEntity<List<CarResponseDTO>> getCarsByUserId(@PathVariable("userId") String userId){
        List<Car> carList = carService.getCarsByUserId(userId);
        List<CarResponseDTO> carResponseDTOS = convertToCarResponseDTO(carList);
        if (carList.size() == 0) {
            return ResponseEntity.notFound().build(); // 찾을 수 없는 경우 404 응답
        }
        System.out.println(carResponseDTOS.get(0).getCarId());
        return ResponseEntity.ok(carResponseDTOS); // 찾은 경우 200 응답과 함께 자동차 및 이미지 정보 목록 반환
    }

    @GetMapping(path = "/users/{userId}/cars/{carId}")
    public ResponseEntity<CarResponseDTO> getCarByCarId(@PathVariable("userId") String userId,
                                                        @PathVariable("carId") Long carId){
        Car car = carRegistrationService.findCarById(carId);
        CarResponseDTO carResponseDTO = new CarResponseDTO();
        carResponseDTO.convertToCarDTO(car);
        return ResponseEntity.ok(carResponseDTO);
    }

    @DeleteMapping(path = "/users/{userId}/cars/{carId}")
    public ResponseEntity<String> deleteCarByCarId(@PathVariable("userId") String userId,
                                                        @PathVariable("carId") Long carId){
        carRegistrationService.deleteCarByCarId(userId, carId);
        System.out.println("delete success");
        return ResponseEntity.ok("success");
    }

    private List<CarResponseDTO> convertToCarResponseDTO(List<Car> carList) {
        List<CarResponseDTO> carResponseDTOS = new ArrayList<>();
        for(Car car : carList){
            CarResponseDTO carResponseDTO = new CarResponseDTO();
            carResponseDTO.convertToCarDTO(car);
            carResponseDTOS.add(carResponseDTO);
        }
        return carResponseDTOS;
    }

    // 차 가격 추천을 위한 머신러닝
    @GetMapping("/train")
    public String trainModel() {
        try {
            carMachineLearningService.trainModel();
            return "Model trained successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error training model";
        }
    }

    @GetMapping("/predict")
    public double predictPrice(@RequestParam String doroAddress,
                               @RequestParam String model,
                               @RequestParam String category,
                               @RequestParam double rating) {
        try {
            return carMachineLearningService.predictPrice(doroAddress, model, category, rating);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
