package no.dusken.momus.service;

import no.dusken.momus.model.Advert;
import no.dusken.momus.service.repository.AdvertRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

public class AdvertServiceTest extends AbstractServiceTest {

    @InjectMocks
    private AdvertService advertService;

    @Mock
    private AdvertRepository advertRepository;

    private Advert advert;

    @Before
    public void setup() {
        advert = Advert.builder().name("iBok").comment("er kult").build();
        advert.setId(0L);

        when(advertRepository.findById(0L)).thenReturn(Optional.of(advert));
        when(advertRepository.saveAndFlush(any(Advert.class)))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));
    }

    @Test
    public void getAdvertById() {
        Advert a = advertService.getAdvertById(0L);

        assert a.equals(advert);
    }

    @Test
    public void saveAdvert() {
        Advert a = Advert.builder().name("dusken.no").comment("oh yes").build();

        advertService.createAdvert(a);

        verify(advertRepository, times(1)).saveAndFlush(a);
    }

    @Test
    public void updateAdvert() {
        advertService.updateAdvert(advert);

        verify(advertRepository, times(1)).saveAndFlush(advert);
    }

    @Test
    public void updateComment() {
        AdvertService advertServiceSpy = spy(advertService);

        Advert a = advertServiceSpy.updateComment(0L, "ny kommentar!");

        verify(advertServiceSpy, times(1)).updateAdvert(advert);

        assertEquals(a.getComment(), "ny kommentar!");
    }
}
