package edu.oregonstate.mist.locations.frontend

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.locations.frontend.db.LocationDAO
import edu.oregonstate.mist.locations.frontend.jsonapi.ResourceObject
import edu.oregonstate.mist.locations.frontend.jsonapi.ResultObject
import edu.oregonstate.mist.locations.frontend.resources.LocationResource
import groovy.mock.interceptor.MockFor
import org.junit.Test
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.After
import io.dropwizard.testing.junit.ResourceTestRule

class LocationResourceTestWithRule {
    static def user = new AuthenticatedUser('nobody')
    static def locationResource = new LocationResource(null)

    @ClassRule
    public static ResourceTestRule resources = ResourceTestRule.builder()
        .addResource(locationResource)
        .build()

    @BeforeClass
    public static void setUp() {
        Resource.loadProperties('resource.properties')
        locationResource.uriInfo = new MockUriInfo()
    }

    @Test
    public void testNoResults() {
        def mock = new MockFor(LocationDAO)
        mock.demand.search() {
            String q, String campus, String type, Integer pageNumber, Integer pageSize ->
                '{"hits": {"total": 0, "hits": []}}'
        }
        def dao = mock.proxyInstance()
        locationResource.locationDAO = dao

        def result = resources.client().target("/locations?q=dixon").request().get(ResultObject)
        assert result.links == [:]
        assert result.data == []

        mock.verify(dao)
    }

    @Test
    public void testInvalidCampus() {
        def mock = new MockFor(LocationDAO)
        def dao = mock.proxyInstance()
        locationResource.locationDAO = dao

        def response = resources.client().target("/locations?q=dixon&campus=invalid").request().get()
        assert response.status == 404

        mock.verify(dao)
    }
}
