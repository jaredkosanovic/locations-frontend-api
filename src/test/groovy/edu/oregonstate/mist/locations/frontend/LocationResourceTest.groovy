package edu.oregonstate.mist.locations.frontend

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.locations.frontend.db.LocationDAO
import edu.oregonstate.mist.locations.frontend.jsonapi.ResourceObject
import edu.oregonstate.mist.locations.frontend.jsonapi.ResultObject
import edu.oregonstate.mist.locations.frontend.resources.LocationResource
import groovy.mock.interceptor.MockFor
import org.junit.Test

class LocationResourceTest {
    def user = new AuthenticatedUser('nobody')

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

        def got = resource.list('dixon', null, null, user).entity
        assert got.links == [:]
        assert got.data == []

        mock.verify(dao)
    }

}
