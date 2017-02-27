/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* tslint:disable:no-unused-variable */

import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { GafferService } from '../services/gaffer.service';
import { Router, ActivatedRoute } from '@angular/router';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { HttpModule } from '@angular/http';
import { LocalStorageService } from 'ng2-webstorage';
import { Observable } from 'rxjs';
import { TypesComponent } from './types.component';

class MockGafferService {
  getCommonTypes() {
    return Observable.from([{ 'test': 1 }]);
  }
}

describe('TypesComponent', () => {
  let component: TypesComponent;
  let fixture: ComponentFixture<TypesComponent>;
  let routerStub = {} as Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        TypesComponent
      ],
      imports: [
        HttpModule
      ],
      providers: [
        LocalStorageService
      ]
    }).overrideComponent(TypesComponent, {
      set: {
        providers: [
          {provide: GafferService, useClass: MockGafferService},
          {provide: ActivatedRoute, useValue: { 'params': Observable.from([{ 'id': 1 }]) }},
          {provide: Router, useValue: routerStub},
        ]
    }})
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TypesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
