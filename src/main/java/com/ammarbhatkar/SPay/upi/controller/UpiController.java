package com.ammarbhatkar.SPay.upi.controller;

import com.ammarbhatkar.SPay.payment.dto.request.UpiPaymentRequest;
import com.ammarbhatkar.SPay.upi.dto.request.CreateUpiHandleRequest;
import com.ammarbhatkar.SPay.upi.dto.response.UpiHandleResponse;
import com.ammarbhatkar.SPay.upi.service.UpiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/upi")
@RequiredArgsConstructor
public class UpiController {
    private final UpiService upiService;


    @PostMapping("/handles")
    public ResponseEntity<UpiHandleResponse>createHandle(@RequestBody @Valid CreateUpiHandleRequest request){
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(upiService.createHandle(request));
    }

    @GetMapping("/resolve/{upiId}")
    public  ResponseEntity<UpiHandleResponse>resolve(@PathVariable String upiId){
        return ResponseEntity.ok(upiService.resolve(upiId));
    }
}
