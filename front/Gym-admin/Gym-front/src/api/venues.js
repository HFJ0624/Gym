import request from '@/utils/frontRequest'

// 获得所有场馆
export const GetAllVenue = (queryDto) => {
  return request({
    url: `/front/venues`,
    method: 'post',
    data: queryDto
  })
}

// 记录场馆访问次数
export const RecordVenueVisit = (venueId) => {
  return request({
    url: `/front/venues/visit/${venueId}`,
    method: 'post'
  })
}





