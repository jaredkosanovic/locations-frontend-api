package edu.oregonstate.mist.locations.frontend

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.locations.frontend.db.LocationDAO
import edu.oregonstate.mist.locations.frontend.resources.LocationResource
import groovy.mock.interceptor.MockFor
import org.junit.Test

class LocationResourceTest {
    static def user = new AuthenticatedUser('nobody')

    @Test
    public void testList() {
        def mock = new MockFor(LocationDAO)
        mock.demand.search() {
            String q, String campus, String type, Integer pageNumber, Integer pageSize ->
                '{"hits": {"total": 0, "hits": []}}'
        }
        def dao = mock.proxyInstance()
        def resource = new LocationResource(dao)
        resource.uriInfo = new MockUriInfo()

        // test case: no result
        def noResultRsp = resource.list('dixon', null, null, user)
        assert noResultRsp.status == 200
        assert noResultRsp.entity.links == [:]
        assert noResultRsp.entity.data == []

        // test case: invalid campus
        def invalidCampRes = resource.list('dixon', 'invalid', null, user)
        assert invalidCampRes.status == 404
        assert invalidCampRes.entity.developerMessage.contains("Not Found")
        assert invalidCampRes.entity.userMessage.contains("Not Found")
        assert invalidCampRes.entity.code == 1404

        mock.verify(dao)
    }

    @Test
    public void testGetById() {
        def mock = new MockFor(LocationDAO)
        mock.demand.getById() {
            String id -> null
        }
        def dao = mock.proxyInstance()
        def resource = new LocationResource(dao)
        resource.uriInfo = new MockUriInfo()

        def response = resource.getById(null, user)
        assert response.status == 404
        assert response.entity.developerMessage.contains("Not Found")
        assert response.entity.userMessage.contains("Not Found")
        assert response.entity.code == 1404

        mock.verify(dao)
    }

    @Test
    public void testSanitize() {
        assert LocationResource.sanitize("Valley[!#]Library") == "Valley    Library"
        assert !LocationResource.sanitize(null)
    }
}
