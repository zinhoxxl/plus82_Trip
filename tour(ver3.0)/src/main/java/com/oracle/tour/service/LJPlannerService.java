package com.oracle.tour.service;


import java.text.ParseException;
import java.util.List;


import com.oracle.tour.domain.Planner;
import com.oracle.tour.dto.PlannerDTO;

/*
 * 이진호
 * 플래너 Service 
 */
public interface LJPlannerService {
    PlannerDTO insertPlanner(PlannerDTO dto) throws ParseException;
    List<PlannerDTO> selectPlanners(String id);
    PlannerDTO selectPlanner(Long plannerNo);
    void deletePlanner(Long plannerNo);

    default Planner dtoToEntity(PlannerDTO dto) throws ParseException {  // java8부터 default로 interface 내에서 메소드 실행이 가능
        Planner entity = null;

        if(dto.getPlannerNo() != null){     // 플래너 수정할 경우 플래너 번호도 같이 전달
            entity = Planner.builder() // builder 패턴은 필요한 데이터만 생성 할 수 있다.
                    .plannerNo(dto.getPlannerNo())
                    .id(dto.getId())
                    .title(dto.getTitle())
                    .fDate(dto.getFDate())
                    .lDate(dto.getLDate())
                    .intro(dto.getIntro())
                    .wDate(dto.getWDate())
                    .build();
        }else {                         // 플래너 생성할 경우 dto에는 플래너 번호가 없기에 제외
            entity = Planner.builder()
                    .id(dto.getId())
                    .title(dto.getTitle())
                    .fDate(dto.getFDate())
                    .lDate(dto.getLDate())
                    .intro(dto.getIntro())
                    .build();
        }
        return entity;
    }

    default PlannerDTO entityToDto(Planner planner) {
        PlannerDTO dto = PlannerDTO.builder()
                .plannerNo(planner.getPlannerNo())
                .id(planner.getId())
                .title(planner.getTitle())
                .intro(planner.getIntro())
                .fDate(planner.getFDate())
                .lDate(planner.getLDate())
                .wDate(planner.getWDate())
                .build();
        return dto;
    }
}
