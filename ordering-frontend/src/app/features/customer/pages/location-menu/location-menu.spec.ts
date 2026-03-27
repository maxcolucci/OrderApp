import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationMenu } from './location-menu';

describe('LocationMenu', () => {
  let component: LocationMenu;
  let fixture: ComponentFixture<LocationMenu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationMenu]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LocationMenu);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
