package com.Siddhant.UserApp.Service.Impl;


import com.Siddhant.UserApp.Repository.ForgetUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.Siddhant.UserApp.Entity.User;;
import com.Siddhant.UserApp.Service.ForgotPasswordService;
import com.Siddhant.UserApp.dto.ForgotPasswordRequest;


@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService{


    @Autowired
    private ForgetUserRepository ForgetuserRepository;



    @Override
    public String updatePassword(ForgotPasswordRequest request) {


        User user = ForgetuserRepository.findByEmail(request.getEmail());


        if(user == null){

            return "Email not registered";

        }


        if(!request.getPassword()
                .equals(request.getConfirmPassword())){


            return "Password not match";

        }



        user.setPassword(request.getPassword());


        ForgetuserRepository.save(user);


        return "Password updated successfully";


    }

}
