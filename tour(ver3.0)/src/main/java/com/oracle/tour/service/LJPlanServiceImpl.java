package com.oracle.tour.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.oracle.tour.dao.LJPlanRepository;
import com.oracle.tour.domain.Plan;
import com.oracle.tour.dto.PlanDTO;
import com.oracle.tour.dto.PlannerDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * 이진호
 * 플랜(상세) Service 
 */
@Service
public class LJPlanServiceImpl implements LJPlanService {
    @Autowired
    private LJPlanRepository repository;

    public void insertPlan(List<PlanDTO> planDTOS){
        List<Plan> plans = new ArrayList<Plan>();
           		  // 변수 : 배열 변수
        for(PlanDTO dto : planDTOS){ // 향상된 for문은 배열 변수만 사용 가능 하기 때문에 이를 활용
            plans.add(dtoToEntity(dto));
        }
        repository.saveAll(plans); 
    }

    
    
    /* 
     * 플래너 번호에 따른 플랜들을 List 타입으로 반환하는 메소드 
     * */
    @Override
    public List<PlanDTO> selectPlan(Long plannerNo) {
        List<Plan> plans = repository.findAllByPlannerNoOrderByPlanNo(plannerNo);
        List<PlanDTO> planDTOS = new ArrayList<PlanDTO>();
        for(Plan plan : plans){
            planDTOS.add(entityToDto(plan));
        }
        return planDTOS;
    }

    
    /* 
     * 플래너의 첫번째 날짜 일정들을 반환하는 메소드 (planList.jsp로 이동시 사용) 
     * */
    @Override
    public List<PlanDTO> selectPlans(PlannerDTO planner) {
        Date now = planner.getFDate();
        Date start = planner.getFDate();
        Date last = new Date(now.getYear(), now.getMonth(), now.getDate(), 23, 59,59);

        List<Plan> result = repository.findAllByPlannerNoAndDayBetweenOrderByPlanNo(planner.getPlannerNo(), start, last);
        List<PlanDTO> dto = new ArrayList<PlanDTO>();
        for(Plan plan : result){
            dto.add(entityToDto(plan));
        }
        return dto;
    }

    
    /* 
     * 사용자가 작성한 각 플래너마다 플랜들을 구한 후 join하여 1개의 List 타입으로 반환하는 메소드  
     * */
    @Override
    public List<PlanDTO> joinPlans(List<PlannerDTO> planners) {
        List<PlanDTO> allPlans = new ArrayList<PlanDTO>();
        for(int i=0;i<planners.size();i++){
            List<PlanDTO> plans = selectPlans(planners.get(i));
            allPlans.addAll(plans);
        }
        return allPlans;
    }

    /*
     * 플래너 삭제
     * */
    @Override
    public void deletePlans(Long plannerNo) {
        repository.deleteAllByPlannerNo(plannerNo);
    }

    
    /* 
     * plnaInsert.jsp에서 전달받은 값들을 List<Plan>으로 반환 
     * */
    public List<PlanDTO> returnPlan(List<Date> days, List<String> place, List<String> place_intro, List<Float> X, List<Float> Y, Long planner_no, String id){
        List<PlanDTO> plans = new ArrayList<PlanDTO>();
        for(int i=0;i<days.size();i++){
            if((days.size()==1) && place_intro.size() == 0){ // 여행 날짜 1일, 일정 1개일때 일정 설명이 null일경우
                PlanDTO plan = PlanDTO.builder()
                        .plannerNo(planner_no)
                        .id(id)
                        .day(days.get(i))
                        .name(place.get(i))
                        .intro(" ") // intro 가 null
                        .x(X.get(i))
                        .y(Y.get(i))
                        .build();
                plans.add(plan);
                System.out.println(place_intro.size());
            }else{
                PlanDTO plan = PlanDTO.builder()
                        .plannerNo(planner_no)
                        .id(id)
                        .day(days.get(i))
                        .name(place.get(i))
                        .intro(place_intro.get(i)) // intro 값이 존재
                        .x(X.get(i))
                        .y(Y.get(i))
                        .build();
                plans.add(plan);
                System.out.println(place_intro.size());
            }
        }
        return  plans;
    }

    
    /* 
     * planUpdate.jsp에서 전달받은 값들을 List<Plan>으로 반환 
     * */
    public List<PlanDTO> returnUpdatePlan(List<Date> days, List<String> place, List<String> place_intro, List<Float> X, List<Float> Y,List<Long> planNo, Long planner_no, String id){
        List<PlanDTO> plans = new ArrayList<PlanDTO>();

        for(int i=0;i<days.size();i++){
            if((days.size()==1) && place_intro.size() == 0){  // 여행 날짜 1일, 일정 1개일때 일정 설명이 null일경우
                if(planNo.size() == 0){                       // DB에 이미 저장된 plan이 아닐경우
                    PlanDTO plan = PlanDTO.builder()
                            .plannerNo(planner_no)
                            .id(id)
                            .day(days.get(i))
                            .name(place.get(i))
                            .intro(" ")
                            .x(X.get(i))
                            .y(Y.get(i))
                            .build();
                    plans.add(plan);
                }else{                                        // DB에 이미 저장된 plan일 경우
                    PlanDTO plan = PlanDTO.builder()
                            .plannerNo(planner_no)
                            .planNo(planNo.get(i)) // 기존 planNo(플랜번호) 가져오기
                            .id(id)
                            .day(days.get(i))
                            .name(place.get(i))
                            .intro(" ")
                            .x(X.get(i))
                            .y(Y.get(i))
                            .build();
                    plans.add(plan);
                }
            }else{ // intro가 있을 경우
                if(planNo.size() ==0){                  // DB에 이미 저장된 plan이 아닐경우
                    PlanDTO plan = PlanDTO.builder()
                            .plannerNo(planner_no)
                            .id(id)
                            .day(days.get(i))
                            .name(place.get(i))
                            .intro(place_intro.get(i))
                            .x(X.get(i))
                            .y(Y.get(i))
                            .build();
                    plans.add(plan);
                }else{                                      // DB에 이미 저장된 plan일 경우
                    PlanDTO plan = PlanDTO.builder()
                            .plannerNo(planner_no)
                            .planNo(planNo.get(i)) // 기존 planNo(플랜번호) 가져오기
                            .id(id)
                            .day(days.get(i))
                            .name(place.get(i))
                            .intro(place_intro.get(i))
                            .x(X.get(i))
                            .y(Y.get(i))
                            .build();
                    plans.add(plan);
                }
            }
        }

        return  plans;
    }

    /*
     * 플래너 수정
     * */
    public void updatePlans(List<PlanDTO> plans, Long plannerNo){
        List<PlanDTO> origin_plans = selectPlan(plannerNo);
        List<Long> origin_planNo = new ArrayList<Long>();

        for(PlanDTO plan : origin_plans){		// origin_plans(기존 plan들)을 PlanDTO plan에 저장
                origin_planNo.add(plan.getPlanNo()); // plan(dto)에서 PlanNo를 origin_planNo에 저장
        }

        List<Long> update_planNo = new ArrayList<Long>();

        for(PlanDTO plan : plans){ // 존재하는 plan만큼 반복
            if(plan.getPlanNo() != null){       // 이미 저장된 plan이 아닌 새로 추가된 plan이라면 제외
                update_planNo.add(plan.getPlanNo());
            }
        }

        if(update_planNo.size() == 0){          // 수정한 플래너에 기존 plan이 1개도 없을 경우
            for(Long num : origin_planNo){
                repository.deleteByPlanNo(num);
            }
        }else{

            for(Long num : update_planNo){          // 수정한 플래너에 있는 기존의 plan과 기존 플래너의 플랜들과 비교
                if(origin_planNo.contains(num)){    // 비교하며 삭제되어야 할 플랜의 번호를 분류
                    origin_planNo.remove(num);
                }
            }

            for(Long num : origin_planNo){          // 분류 완료 후 삭제 되어야 할 planNo만 남은 상태
                repository.deleteByPlanNo(num);
            }

        }
    }

    
    public int getDiffDayCount(Date fromDate, Date toDate){
        return (int)((toDate.getTime() - fromDate.getTime()) / 1000 / 60 / 60 / 24);
    }
    public List<Date> getDiffDays(Date fromDate, Date toDate){
    	                    // .getInstance() : 최초에 할당된 하나의 메모리를 계속 쓰는 방식
        Calendar cal = Calendar.getInstance(); // 캘린더 생성
        cal.setTime(fromDate); // 시작일을 캘린더에 저장
        int count = getDiffDayCount(fromDate,toDate);
        // 시작일부터
        cal.add(Calendar.DATE, -1); // 하루 빼기 : 이렇게 해야 배열(내가 선택한 day만큼의 범위가 나옴, 안그러면 내가 시작한 날부터 +1된 날짜가 반복분의 시작점이 됨
        // 데이터 저장
        List result = new ArrayList();
        for(int i=0; i<=count; i++){
            cal.add(Calendar.DATE, 1); // 하루씩 저장 : 반복문 실행전에 위에서 -1 해놨기때문에 +1하면 일정만큼이 정확하게 저장
            result.add(cal.getTime());
        }
        return result;
    }

    
    public List<Date> changeDateList(List<String> dates, List<String> times) throws ParseException {
        List<Date> result = new ArrayList<Date>();

        for(int i=0;i<dates.size();i++){         // SimpleDateFormat 은 String -> Date 로 변환 해주는 메소드                   
            SimpleDateFormat recvSimpleFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            SimpleDateFormat tranSimpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            // String 형식을 Date형식으로 변환한다.
            Date dateDate = recvSimpleFormat.parse(dates.get(i));
            // Date 값을 가져와서 String으로 변환한다.
            String stringDate = tranSimpleFormat.format(dateDate);

            String stringDay = stringDate + " " + times.get(i); // 이렇게 하는 이유 -> date 하나에 입력하능한 일정(time)은 최대 9개니까 
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // String 형식을 Date형식으로 변환한다.
            Date dateDay = format.parse(stringDay);
            // List에 저장 (for문이니까 dates의 길이 만큼 반복해서 List에 저장
            result.add(dateDay);
        }

        return result;
    }

    
    public List<Date> changeDateListForUpdate(List<String> dates, List<String> times) throws ParseException {
        List<Date> result = new ArrayList<Date>();

        for(int i=0;i<dates.size();i++){
            String stringDay = dates.get(i) + " " + times.get(i);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date dateDay = format.parse(stringDay);
            result.add(dateDay);
        }

        return result;
    }

    
    public List<Float> changeFloatList(List<String> data){
        List<Float> result = new ArrayList<Float>();
        for(int i=0;i<data.size();i++){
            Float floatNum = Float.parseFloat(data.get(i)); // parseFloat => 주어진 값을 필요한 경우 문자열로 변환한 후 부동소수점 실수로 파싱해 반환
            result.add(floatNum);
        }
        return result;
    }

    
    public List<Long> changeLongList(List<String> data){
        List<Long> result = new ArrayList<Long>();
        for(int i=0;i<data.size();i++){
            if(!(data.get(i).isEmpty())){
                Long longNum = Long.parseLong(data.get(i));
                result.add(longNum);
            }else{
                result.add(null);
            }
        }
        return result;
    }
}
