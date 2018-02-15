/**
 * Copyright (c) 2016-2018 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.stk.pm.comm

import com.jcabi.xml.XML
import com.zerocracy.Farm
import com.zerocracy.Par
import com.zerocracy.Project
import com.zerocracy.farm.Assume
import com.zerocracy.pm.ClaimIn
import com.zerocracy.pmo.Awards
import com.zerocracy.pmo.People

def exec(Project project, XML xml) {
  new Assume(project, xml).type('Notify all')
  new Assume(project, xml).notPmo()
  ClaimIn claim = new ClaimIn(xml)
  int min = Integer.parseInt(claim.param('min'))
  Farm farm = binding.variables.farm
  People people = new People(farm).bootstrap()
  for (String uid : people.iterate()) {
    if (people.vacation(uid)) {
      continue
    }
    if (new Awards(farm, uid).bootstrap().total() < min) {
      continue
    }
    claim.copy()
      .type('Notify user')
      .token("user;${uid}")
      .param(
        'message',
        claim.param('message') + new Par(
          '\n\nYou received this message because your reputation is over %d',
          'and you are not on vacation, as in §38.'
        ).say(min)
      )
      .postTo(project)
  }
}